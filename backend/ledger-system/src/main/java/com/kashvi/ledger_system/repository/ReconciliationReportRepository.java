package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.ReconciliationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconciliationReportRepository
        extends JpaRepository<ReconciliationReport, Long> {

    List<ReconciliationReport> findTop10ByOrderByReconciliationTimestampDesc();
}
