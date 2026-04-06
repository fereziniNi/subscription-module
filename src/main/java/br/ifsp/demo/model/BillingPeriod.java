package br.ifsp.demo.model;

import java.time.LocalDate;

public class BillingPeriod {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public BillingPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
