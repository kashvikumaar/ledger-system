package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconciliationRecordRepository
        extends JpaRepository<ReconciliationRecord, Long> {

    List<ReconciliationRecord> findByReportId(Long reportId);
}
