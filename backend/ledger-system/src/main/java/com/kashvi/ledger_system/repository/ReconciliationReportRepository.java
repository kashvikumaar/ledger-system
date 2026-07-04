package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.ReconciliationReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationReportRepository
        extends JpaRepository<ReconciliationReport, Long> {
}
