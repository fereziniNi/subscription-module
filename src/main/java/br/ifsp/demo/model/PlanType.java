package br.ifsp.demo.model;

import java.math.BigDecimal;

public enum PlanType {
    BASIC(new BigDecimal("29.90")),
    PLUS(new BigDecimal("49.90")),
    PRO(new BigDecimal("79.90"));

    private final BigDecimal monthlyPrice;

    PlanType(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
}
