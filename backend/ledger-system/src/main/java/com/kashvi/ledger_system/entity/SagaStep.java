package com.kashvi.ledger_system.entity;

import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.enums.StepType;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saga_steps",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_saga_step_type",
                columnNames = {"saga_id", "step_type"}
        )
)
public class SagaStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id", nullable = false)
    private Saga saga;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private StepType stepType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStepStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Version
    private Long version;

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

    public Long getVersion() {
        return version;
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
