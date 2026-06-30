package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.EntryRequest;
import com.kashvi.ledger_system.dto.TransactionRequest;
import com.kashvi.ledger_system.entity.Account;
import com.kashvi.ledger_system.entity.Entry;
import com.kashvi.ledger_system.entity.LedgerTransaction;
import com.kashvi.ledger_system.repository.AccountRepository;
import com.kashvi.ledger_system.repository.EntryRepository;
import com.kashvi.ledger_system.repository.LedgerTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final EntryRepository entryRepository;
    private final LedgerTransactionRepository transactionRepository;

    public Long getBalance(Long accountId) {
        return entryRepository.getBalance(accountId);
    }

    public TransactionService(
            AccountRepository accountRepository,
            EntryRepository entryRepository,
            LedgerTransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void createTransaction(TransactionRequest request) {

        long totalCredits = 0;
        long totalDebits = 0;


        for (EntryRequest entry : request.getEntries()) {

            if (entry.getType().equals("CREDIT")) {
                totalCredits += entry.getAmount();
            } else if (entry.getType().equals("DEBIT")) {
                totalDebits += entry.getAmount();
            }
        }

        if (totalCredits != totalDebits) {
            throw new RuntimeException("Transaction is not balanced");
        }

        LedgerTransaction transaction = new LedgerTransaction();
        transaction.setStatus("PENDING");

        transaction = transactionRepository.save(transaction);

        for (EntryRequest entryRequest : request.getEntries()) {

            Account account = accountRepository.findById(
                    entryRequest.getAccountId()
            ).orElseThrow(() ->
                    new RuntimeException("Account not found")
            );

            Entry entry = new Entry();

            entry.setTransaction(transaction);
            entry.setAccount(account);
            entry.setType(entryRequest.getType());
            entry.setAmount(entryRequest.getAmount());

            entryRepository.save(entry);
        }

        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);
    }
}