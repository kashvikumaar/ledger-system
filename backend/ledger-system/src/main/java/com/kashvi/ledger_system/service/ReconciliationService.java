package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.ReconciliationRecordResponse;
import com.kashvi.ledger_system.dto.ReconciliationReportResponse;
import com.kashvi.ledger_system.entity.LedgerTransaction;
import com.kashvi.ledger_system.entity.ReconciliationRecord;
import com.kashvi.ledger_system.entity.ReconciliationReport;
import com.kashvi.ledger_system.enums.ReconciliationStatus;
import com.kashvi.ledger_system.repository.EntryRepository;
import com.kashvi.ledger_system.repository.LedgerTransactionRepository;
import com.kashvi.ledger_system.repository.ReconciliationRecordRepository;
import com.kashvi.ledger_system.repository.ReconciliationReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReconciliationService {

    private final ReconciliationReportRepository reportRepository;
    private final ReconciliationRecordRepository recordRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final EntryRepository entryRepository;

    public ReconciliationService(
            ReconciliationReportRepository reportRepository,
            ReconciliationRecordRepository recordRepository,
            LedgerTransactionRepository transactionRepository,
            EntryRepository entryRepository
    ) {
        this.reportRepository = reportRepository;
        this.recordRepository = recordRepository;
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public ReconciliationReportResponse reconcile(MultipartFile file) {
        List<ExternalTransaction> externalTransactions =
                parseCsv(file);

        ReconciliationReport report =
                createEmptyReport();

        Map<String, List<ExternalTransaction>> groupedExternalTransactions =
                groupByReference(externalTransactions);

        Set<String> referencedLedgerTransactions =
                new HashSet<>();

        List<ReconciliationRecord> records =
                new ArrayList<>();

        for (Map.Entry<String, List<ExternalTransaction>> entry
                : groupedExternalTransactions.entrySet()) {

            List<ExternalTransaction> groupedRows =
                    entry.getValue();

            if (groupedRows.size() > 1) {
                addDuplicateRecords(report, records, groupedRows);
                continue;
            }

            ExternalTransaction externalTransaction =
                    groupedRows.get(0);

            records.add(
                    reconcileExternalTransaction(
                            report,
                            externalTransaction,
                            referencedLedgerTransactions
                    )
            );
        }

        addMissingExternalRecords(
                report,
                records,
                referencedLedgerTransactions
        );

        recordRepository.saveAll(records);
        updateReportCounts(report, records);

        return getReport(report.getId());
    }

    public ReconciliationReportResponse getReport(Long reportId) {
        ReconciliationReport report =
                reportRepository.findById(reportId)
                        .orElseThrow(() ->
                                new RuntimeException("Reconciliation report not found")
                        );

        List<ReconciliationRecord> records =
                recordRepository.findByReportId(reportId);

        return toReportResponse(report, records);
    }

    private ReconciliationReport createEmptyReport() {
        ReconciliationReport report =
                new ReconciliationReport();

        report.setTotalRecords(0L);
        report.setMatched(0L);
        report.setUnmatched(0L);
        report.setDuplicates(0L);
        report.setAmountMismatches(0L);

        return reportRepository.save(report);
    }

    private ReconciliationRecord reconcileExternalTransaction(
            ReconciliationReport report,
            ExternalTransaction externalTransaction,
            Set<String> referencedLedgerTransactions
    ) {
        Optional<LedgerTransaction> transaction =
                transactionRepository.findByExternalTransactionReference(
                        externalTransaction.transactionReference()
                );

        if (transaction.isEmpty()) {
            return createRecord(
                    report,
                    externalTransaction,
                    null,
                    ReconciliationStatus.MISSING_IN_LEDGER,
                    "Transaction not found in ledger"
            );
        }

        Long internalAmount =
                entryRepository.getTransactionAmount(
                        transaction.get().getId()
                );

        referencedLedgerTransactions.add(
                transaction.get().getExternalTransactionReference()
        );

        if (!internalAmount.equals(externalTransaction.amount())) {
            return createRecord(
                    report,
                    externalTransaction,
                    internalAmount,
                    ReconciliationStatus.AMOUNT_MISMATCH,
                    "External amount does not match ledger amount"
            );
        }

        return createRecord(
                report,
                externalTransaction,
                internalAmount,
                ReconciliationStatus.MATCHED,
                "External record matches ledger"
        );
    }

    private void addDuplicateRecords(
            ReconciliationReport report,
            List<ReconciliationRecord> records,
            List<ExternalTransaction> duplicateRows
    ) {
        for (ExternalTransaction duplicateRow : duplicateRows) {
            records.add(
                    createRecord(
                            report,
                            duplicateRow,
                            null,
                            ReconciliationStatus.DUPLICATE_TRANSACTION,
                            "Duplicate transaction reference in external file"
                    )
            );
        }
    }

    private void addMissingExternalRecords(
            ReconciliationReport report,
            List<ReconciliationRecord> records,
            Set<String> referencedLedgerTransactions
    ) {
        List<LedgerTransaction> successfulTransactions =
                transactionRepository.findSuccessfulTransactionsWithExternalReference();

        for (LedgerTransaction transaction : successfulTransactions) {
            if (referencedLedgerTransactions.contains(
                    transaction.getExternalTransactionReference()
            )) {
                continue;
            }

            ReconciliationRecord record =
                    new ReconciliationRecord();

            record.setReport(report);
            record.setTransactionReference(
                    transaction.getExternalTransactionReference()
            );
            record.setInternalAmount(
                    entryRepository.getTransactionAmount(transaction.getId())
            );
            record.setStatus(ReconciliationStatus.MISSING_EXTERNALLY);
            record.setDetails("Ledger transaction missing from external file");

            records.add(record);
        }
    }

    private ReconciliationRecord createRecord(
            ReconciliationReport report,
            ExternalTransaction externalTransaction,
            Long internalAmount,
            ReconciliationStatus status,
            String details
    ) {
        ReconciliationRecord record =
                new ReconciliationRecord();

        record.setReport(report);
        record.setTransactionReference(
                externalTransaction.transactionReference()
        );
        record.setExternalAmount(externalTransaction.amount());
        record.setInternalAmount(internalAmount);
        record.setCurrency(externalTransaction.currency());
        record.setExternalTimestamp(externalTransaction.timestamp());
        record.setStatus(status);
        record.setDetails(details);

        return record;
    }

    private void updateReportCounts(
            ReconciliationReport report,
            List<ReconciliationRecord> records
    ) {
        long matched =
                count(records, ReconciliationStatus.MATCHED);

        long duplicates =
                count(records, ReconciliationStatus.DUPLICATE_TRANSACTION);

        long amountMismatches =
                count(records, ReconciliationStatus.AMOUNT_MISMATCH);

        long unmatched =
                records.size() - matched;

        report.setTotalRecords((long) records.size());
        report.setMatched(matched);
        report.setUnmatched(unmatched);
        report.setDuplicates(duplicates);
        report.setAmountMismatches(amountMismatches);

        reportRepository.save(report);
    }

    private long count(
            List<ReconciliationRecord> records,
            ReconciliationStatus status
    ) {
        return records.stream()
                .filter(record -> record.getStatus() == status)
                .count();
    }

    private List<ExternalTransaction> parseCsv(MultipartFile file) {
        List<ExternalTransaction> transactions =
                new ArrayList<>();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        file.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            String line =
                    reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                transactions.add(parseCsvRow(line));
            }

            return transactions;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse reconciliation CSV", e);
        }
    }

    private ExternalTransaction parseCsvRow(String line) {
        List<String> columns =
                splitCsvLine(line);

        if (columns.size() != 4) {
            throw new RuntimeException("Invalid reconciliation CSV row");
        }

        return new ExternalTransaction(
                columns.get(0).trim(),
                Long.parseLong(columns.get(1).trim()),
                columns.get(2).trim(),
                LocalDateTime.parse(columns.get(3).trim())
        );
    }

    private List<String> splitCsvLine(String line) {
        List<String> columns =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        boolean insideQuotes =
                false;

        for (int i = 0; i < line.length(); i++) {
            char character =
                    line.charAt(i);

            if (character == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }

            if (character == ',' && !insideQuotes) {
                columns.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(character);
        }

        columns.add(current.toString());

        return columns;
    }

    private Map<String, List<ExternalTransaction>> groupByReference(
            List<ExternalTransaction> externalTransactions
    ) {
        Map<String, List<ExternalTransaction>> groupedTransactions =
                new LinkedHashMap<>();

        for (ExternalTransaction externalTransaction : externalTransactions) {
            groupedTransactions
                    .computeIfAbsent(
                            externalTransaction.transactionReference(),
                            key -> new ArrayList<>()
                    )
                    .add(externalTransaction);
        }

        return groupedTransactions;
    }

    private ReconciliationReportResponse toReportResponse(
            ReconciliationReport report,
            List<ReconciliationRecord> records
    ) {
        ReconciliationReportResponse response =
                new ReconciliationReportResponse();

        response.setId(report.getId());
        response.setTotalRecords(report.getTotalRecords());
        response.setMatched(report.getMatched());
        response.setUnmatched(report.getUnmatched());
        response.setDuplicates(report.getDuplicates());
        response.setAmountMismatches(report.getAmountMismatches());
        response.setReconciliationTimestamp(
                report.getReconciliationTimestamp()
        );
        response.setRecords(toRecordResponses(records));

        return response;
    }

    private List<ReconciliationRecordResponse> toRecordResponses(
            List<ReconciliationRecord> records
    ) {
        List<ReconciliationRecordResponse> responses =
                new ArrayList<>();

        for (ReconciliationRecord record : records) {
            responses.add(toRecordResponse(record));
        }

        return responses;
    }

    private ReconciliationRecordResponse toRecordResponse(
            ReconciliationRecord record
    ) {
        ReconciliationRecordResponse response =
                new ReconciliationRecordResponse();

        response.setId(record.getId());
        response.setTransactionReference(record.getTransactionReference());
        response.setExternalAmount(record.getExternalAmount());
        response.setInternalAmount(record.getInternalAmount());
        response.setCurrency(record.getCurrency());
        response.setExternalTimestamp(record.getExternalTimestamp());
        response.setStatus(record.getStatus());
        response.setDetails(record.getDetails());

        return response;
    }

    private record ExternalTransaction(
            String transactionReference,
            Long amount,
            String currency,
            LocalDateTime timestamp
    ) {
    }
}
