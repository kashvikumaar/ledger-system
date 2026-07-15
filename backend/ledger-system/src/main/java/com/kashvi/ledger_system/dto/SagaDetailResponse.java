package com.kashvi.ledger_system.dto;

import java.util.List;

public class SagaDetailResponse extends SagaSummaryResponse {

    private List<SagaStepResponse> steps;

    public SagaDetailResponse() {
    }

    public List<SagaStepResponse> getSteps() {
        return steps;
    }

    public void setSteps(List<SagaStepResponse> steps) {
        this.steps = steps;
    }
}
