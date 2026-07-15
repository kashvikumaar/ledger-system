package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.*;
import com.kashvi.ledger_system.entity.*;
import com.kashvi.ledger_system.enums.SagaStatus;
import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class OperationsService {

    private final AccountRepository accountRepository;
    private final EntryRepository entryRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final SagaRepository sagaRepository;
    private final SagaStepRepository sagaStepRepository;
    private final ReconciliationReportRepository reportRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RedisConnectionFactory redisConnectionFactory;
    private final String applicationVersion;

    public OperationsService(
            AccountRepository accountRepository,
            EntryRepository entryRepository,
            LedgerTransactionRepository transactionRepository,
            SagaRepository sagaRepository,
            SagaStepRepository sagaStepRepository,
            ReconciliationReportRepository reportRepository,
            JdbcTemplate jdbcTemplate,
            RedisConnectionFactory redisConnectionFactory,
            @Value("${spring.application.name}") String applicationVersion
    ) {
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
        this.transactionRepository = transactionRepository;
        this.sagaRepository = sagaRepository;
        this.sagaStepRepository = sagaStepRepository;
        this.reportRepository = reportRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.redisConnectionFactory = redisConnectionFactory;
        this.applicationVersion = applicationVersion;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboard() {
        DashboardSummaryResponse response =
                new DashboardSummaryResponse();

        response.setTotalAccounts(accountRepository.count());
        response.setTotalTransactions(transactionRepository.count());
        response.setSuccessfulTransactions(transactionRepository.countByStatus("SUCCESS"));
        response.setFailedTransactions(transactionRepository.countByStatus("FAILED"));
        response.setActiveSagas(sagaRepository.countByStatusIn(List.of(
                SagaStatus.STARTED,
                SagaStatus.IN_PROGRESS,
                SagaStatus.COMPENSATING
        )));
        response.setPendingRecoveries(sagaRepository.countByStatusIn(recoverableStatuses()));
        response.setRecentTransactions(
                transactionRepository.findTop10ByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::toTransactionSummary)
                        .toList()
        );
        response.setRecentSagas(
                sagaRepository.findTop10ByOrderByUpdatedAtDesc()
                        .stream()
                        .map(this::toSagaSummary)
                        .toList()
        );
        response.setRecentReconciliationReports(
                reportRepository.findTop10ByOrderByReconciliationTimestampDesc()
                        .stream()
                        .map(this::toReportSummary)
                        .toList()
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<AccountSummaryResponse> getAccounts(String search) {
        String normalizedSearch =
                normalize(search);

        return accountRepository.findAllByOrderByIdAsc()
                .stream()
                .filter(account -> matchesAccount(account, normalizedSearch))
                .map(this::toAccountSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LedgerEntryResponse> getAccountEntries(Long accountId) {
        return entryRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .limit(25)
                .map(this::toEntryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionSummaryResponse> getTransactions(
            String status,
            String search,
            Integer page,
            Integer size
    ) {
        int requestedPage =
                page == null || page < 0 ? 0 : page;

        int requestedSize =
                size == null || size < 1 ? 25 : Math.min(size, 100);

        List<TransactionSummaryResponse> filtered =
                transactionRepository.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .filter(transaction -> matchesTransaction(transaction, status, search))
                        .map(this::toTransactionSummary)
                        .toList();

        int fromIndex =
                Math.min(requestedPage * requestedSize, filtered.size());

        int toIndex =
                Math.min(fromIndex + requestedSize, filtered.size());

        return filtered.subList(fromIndex, toIndex);
    }

    @Transactional(readOnly = true)
    public TransactionDetailResponse getTransaction(Long transactionId) {
        LedgerTransaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException("Transaction not found")
                        );

        TransactionDetailResponse response =
                new TransactionDetailResponse();

        copyTransactionSummary(response, transaction);
        response.setEntries(
                entryRepository.findByTransactionId(transactionId)
                        .stream()
                        .map(this::toEntryResponse)
                        .toList()
        );
        response.setSaga(
                sagaRepository.findByTransactionId(transactionId)
                        .map(this::toSagaSummary)
                        .orElse(null)
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<SagaSummaryResponse> getSagas(String status, boolean recoverable) {
        return sagaRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .filter(saga -> matchesSaga(saga, status, recoverable))
                .map(this::toSagaSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public SagaDetailResponse getSaga(Long sagaId) {
        Saga saga =
                sagaRepository.findById(sagaId)
                        .orElseThrow(() ->
                                new RuntimeException("Saga not found")
                        );

        SagaDetailResponse response =
                new SagaDetailResponse();

        copySagaSummary(response, saga);
        response.setSteps(
                sagaStepRepository.findBySagaIdOrderByIdAsc(sagaId)
                        .stream()
                        .map(this::toSagaStepResponse)
                        .toList()
        );

        return response;
    }

    public HealthStatusResponse getHealth() {
        HealthStatusResponse response =
                new HealthStatusResponse();

        response.setBackendStatus("UP");
        response.setDatabaseStatus(databaseStatus());
        response.setRedisStatus(redisStatus());
        response.setCurrentTime(LocalDateTime.now());
        response.setApplicationVersion(applicationVersion);
        response.setFlywayMigrationVersion(flywayVersion());

        return response;
    }

    private AccountSummaryResponse toAccountSummary(Account account) {
        AccountSummaryResponse response =
                new AccountSummaryResponse();

        response.setId(account.getId());
        response.setName(account.getName());
        response.setType(account.getType());
        response.setCurrency(account.getCurrency());
        response.setBalance(entryRepository.getBalance(account.getId()));

        return response;
    }

    private TransactionSummaryResponse toTransactionSummary(
            LedgerTransaction transaction
    ) {
        TransactionSummaryResponse response =
                new TransactionSummaryResponse();

        copyTransactionSummary(response, transaction);

        return response;
    }

    private void copyTransactionSummary(
            TransactionSummaryResponse response,
            LedgerTransaction transaction
    ) {
        response.setId(transaction.getId());
        response.setExternalTransactionReference(
                transaction.getExternalTransactionReference()
        );
        response.setStatus(transaction.getStatus());
        response.setAmount(entryRepository.getTransactionAmount(transaction.getId()));
        response.setCurrency(resolveTransactionCurrency(transaction.getId()));
        response.setCreatedAt(transaction.getCreatedAt());
    }

    private SagaSummaryResponse toSagaSummary(Saga saga) {
        SagaSummaryResponse response =
                new SagaSummaryResponse();

        copySagaSummary(response, saga);

        return response;
    }

    private void copySagaSummary(
            SagaSummaryResponse response,
            Saga saga
    ) {
        response.setId(saga.getId());
        response.setStatus(saga.getStatus().name());
        response.setCurrentStep(resolveCurrentStep(saga.getId()));
        response.setTransactionReference(resolveSagaTransactionReference(saga));
        response.setCreatedAt(saga.getCreatedAt());
        response.setUpdatedAt(saga.getUpdatedAt());
    }

    private SagaStepResponse toSagaStepResponse(SagaStep sagaStep) {
        SagaStepResponse response =
                new SagaStepResponse();

        response.setId(sagaStep.getId());
        response.setStepType(sagaStep.getStepType().name());
        response.setStatus(sagaStep.getStatus().name());
        response.setFailureReason(sagaStep.getFailureReason());
        response.setCreatedAt(sagaStep.getCreatedAt());
        response.setUpdatedAt(sagaStep.getUpdatedAt());

        return response;
    }

    private LedgerEntryResponse toEntryResponse(Entry entry) {
        LedgerEntryResponse response =
                new LedgerEntryResponse();

        response.setId(entry.getId());
        response.setAccountId(entry.getAccount().getId());
        response.setAccountName(entry.getAccount().getName());
        response.setType(entry.getType());
        response.setAmount(entry.getAmount());
        response.setCreatedAt(entry.getCreatedAt());

        return response;
    }

    private ReconciliationReportResponse toReportSummary(
            ReconciliationReport report
    ) {
        ReconciliationReportResponse response =
                new ReconciliationReportResponse();

        response.setId(report.getId());
        response.setTotalRecords(report.getTotalRecords());
        response.setMatched(report.getMatched());
        response.setUnmatched(report.getUnmatched());
        response.setDuplicates(report.getDuplicates());
        response.setAmountMismatches(report.getAmountMismatches());
        response.setReconciliationTimestamp(report.getReconciliationTimestamp());
        response.setRecords(List.of());

        return response;
    }

    private boolean matchesAccount(Account account, String search) {
        if (search == null) {
            return true;
        }

        return account.getName().toLowerCase().contains(search)
                || account.getType().toLowerCase().contains(search)
                || String.valueOf(account.getId()).equals(search);
    }

    private boolean matchesTransaction(
            LedgerTransaction transaction,
            String status,
            String search
    ) {
        String normalizedSearch =
                normalize(search);

        if (status != null && !status.isBlank()
                && !status.equals(transaction.getStatus())) {
            return false;
        }

        if (normalizedSearch == null) {
            return true;
        }

        String reference =
                transaction.getExternalTransactionReference() == null
                        ? ""
                        : transaction.getExternalTransactionReference().toLowerCase();

        return reference.contains(normalizedSearch)
                || String.valueOf(transaction.getId()).equals(normalizedSearch);
    }

    private boolean matchesSaga(
            Saga saga,
            String status,
            boolean recoverable
    ) {
        if (recoverable && !recoverableStatuses().contains(saga.getStatus())) {
            return false;
        }

        return status == null
                || status.isBlank()
                || saga.getStatus().name().equals(status);
    }

    private Collection<SagaStatus> recoverableStatuses() {
        return List.of(
                SagaStatus.STARTED,
                SagaStatus.IN_PROGRESS,
                SagaStatus.COMPENSATING,
                SagaStatus.COMPENSATION_FAILED
        );
    }

    private String resolveCurrentStep(Long sagaId) {
        return sagaStepRepository.findBySagaIdOrderByIdAsc(sagaId)
                .stream()
                .filter(step -> step.getStatus() == SagaStepStatus.PENDING
                        || step.getStatus() == SagaStepStatus.IN_PROGRESS)
                .findFirst()
                .map(step -> step.getStepType().name())
                .orElse("—");
    }

    private String resolveSagaTransactionReference(Saga saga) {
        if (saga.getTransaction() == null) {
            return null;
        }

        return saga.getTransaction().getExternalTransactionReference();
    }

    private String resolveTransactionCurrency(Long transactionId) {
        return entryRepository.findByTransactionId(transactionId)
                .stream()
                .findFirst()
                .map(entry -> entry.getAccount().getCurrency())
                .orElse("INR");
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().toLowerCase();
    }

    private String databaseStatus() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String redisStatus() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            return "PONG".equalsIgnoreCase(pong) ? "UP" : "DEGRADED";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String flywayVersion() {
        try {
            String version =
                    jdbcTemplate.queryForObject(
                            """
                                SELECT version
                                FROM flyway_schema_history
                                WHERE success = true
                                ORDER BY installed_rank DESC
                                LIMIT 1
                            """,
                            String.class
                    );

            return version == null ? "UNKNOWN" : version;
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
