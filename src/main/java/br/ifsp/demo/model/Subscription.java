package br.ifsp.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


public class Subscription {

    private final UUID id;
    private final UUID customerId;
    private  PlanType planType;
    private PlanType scheduledPlanType;
    private BigDecimal proratedChargeAmount;
    private final BillingCycle billingCycle;
    private final SubscriptionStatus status;
    private final BigDecimal amount;
    private final BillingPeriod billingPeriod;


    public Subscription(UUID customerId, PlanType planType, BillingCycle billingCycle, SubscriptionStatus status, BigDecimal amount,  BillingPeriod billingPeriod) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.planType = planType;
        this.billingCycle = billingCycle;
        this.status = status;
        this.amount = amount;
        this.billingPeriod = billingPeriod;
    }

    public void changePlan(PlanType newPlanType, LocalDate currentDate) {
        if (this.status == SubscriptionStatus.SUSPENDED) {
            throw new IllegalStateException("Suspended subscription");
        }
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Inactive subscription");
        }

        if (this.planType == newPlanType) {
            throw new IllegalArgumentException("Plan already contracted");
        }

        if (newPlanType.ordinal() > this.planType.ordinal()) {
            applyImmediateUpgrade(newPlanType, currentDate);
            return;
        }
        scheduleDowngrade(newPlanType);
    }

    private void applyImmediateUpgrade(PlanType newPlanType, LocalDate currentDate) {
        BigDecimal priceDifference = newPlanType.getMonthlyPrice().subtract(this.planType.getMonthlyPrice());
        long totalDays = ChronoUnit.DAYS.between(
                billingPeriod.getStartDate(),
                billingPeriod.getEndDate()
        );
        long remainingDays = ChronoUnit.DAYS.between(
                currentDate,
                billingPeriod.getEndDate()
        );

        this.proratedChargeAmount = priceDifference
                .multiply(BigDecimal.valueOf(remainingDays))
                .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

        this.planType = newPlanType;
        this.scheduledPlanType = null;
    }

    private void scheduleDowngrade(PlanType newPlanType) {
        this.scheduledPlanType = newPlanType;
        this.proratedChargeAmount = null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public PlanType getScheduledPlanType() {
        return scheduledPlanType;
    }

    public BigDecimal getProratedChargeAmount() {
        return proratedChargeAmount;
    }

}
