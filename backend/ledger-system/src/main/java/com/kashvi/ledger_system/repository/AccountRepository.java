package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}