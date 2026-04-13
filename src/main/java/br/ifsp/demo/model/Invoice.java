package br.ifsp.demo.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Invoice {

    private final UUID id;
    private final UUID subscriptionId;
    private final BillingPeriod period;
    private final BigDecimal amount;

    public Invoice(UUID subscriptionId, BillingPeriod period, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.subscriptionId = subscriptionId;
        this.period = period;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public BillingPeriod getPeriod() {
        return period;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}