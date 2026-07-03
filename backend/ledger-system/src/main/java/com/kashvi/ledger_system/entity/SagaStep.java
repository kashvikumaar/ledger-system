package com.kashvi.ledger_system.entity;

import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.enums.StepType;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_steps")
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "saga_id")
    private Saga saga;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type")
    private StepType stepType;

    @Enumerated(EnumType.STRING)
    private SagaStepStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @CurrentTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SagaStep() {
    }

    public Long getId() {
        return id;
    }

    public Saga getSaga() {
        return saga;
    }

    public StepType getStepType() {
        return stepType;
    }

    public SagaStepStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setSaga(Saga saga) {
        this.saga = saga;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public void setStatus(SagaStepStatus status) {
        this.status = status;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
