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
    private SubscriptionStatus status;
    private BigDecimal amount;
    private BillingPeriod billingPeriod;

    private boolean cancellationScheduled;
    private final LocalDate createdAt;


    public Subscription(UUID customerId, PlanType planType, BillingCycle billingCycle, SubscriptionStatus status, BigDecimal amount,  BillingPeriod billingPeriod) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.planType = planType;
        this.billingCycle = billingCycle;
        this.status = status;
        this.amount = amount;
        this.billingPeriod = billingPeriod;
        this.createdAt = LocalDate.now();
    }

    public Subscription(
            UUID customerId,
            PlanType planType,
            BillingCycle billingCycle,
            SubscriptionStatus status,
            BigDecimal amount,
            BillingPeriod billingPeriod,
            LocalDate createdAt
    ) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.planType = planType;
        this.billingCycle = billingCycle;
        this.status = status;
        this.amount = amount;
        this.billingPeriod = billingPeriod;
        this.createdAt = createdAt;
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

    public void renew(boolean paymentApproved, LocalDate currentDate) {

        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled subscription");
        }

        if (currentDate.isBefore(this.billingPeriod.getEndDate())) {
            throw new IllegalStateException("Early renewal");
        }

        if (!paymentApproved) {
            this.status = SubscriptionStatus.SUSPENDED;
            return;
        }

        if (this.scheduledPlanType != null) {
            this.planType = this.scheduledPlanType;
            this.scheduledPlanType = null;
        }

        LocalDate newStartDate = currentDate.isAfter(this.billingPeriod.getEndDate())
                ? currentDate
                : this.billingPeriod.getEndDate();

        if (this.billingCycle == BillingCycle.MONTHLY) {
            this.billingPeriod = new BillingPeriod(newStartDate, newStartDate.plusMonths(1));
            this.amount = this.planType.getMonthlyPrice();
            this.status = SubscriptionStatus.ACTIVE;
            return;
        }

        this.billingPeriod = new BillingPeriod(newStartDate, newStartDate.plusYears(1));
        this.amount = this.planType.getMonthlyPrice()
                .multiply(new BigDecimal("12"))
                .multiply(new BigDecimal("0.60"));
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void cancelImmediately() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled subscription");
        }

        this.status = SubscriptionStatus.CANCELLED;
    }

    public void cancelAtPeriodEnd() {
        this.cancellationScheduled = true;
    }

    public void reverseScheduledCancellation() {
        this.cancellationScheduled = false;
    }

    public void processCycleEnding(LocalDate currentDate) {
        processExpiration(currentDate);
    }

    public boolean processExpiration(LocalDate currentDate) {
        if (!currentDate.isAfter(this.billingPeriod.getEndDate())) {
            return false;
        }

        if (this.status == SubscriptionStatus.CANCELLED) {
            return false;
        }

        if (this.cancellationScheduled) {
            this.status = SubscriptionStatus.CANCELLED;
            return true;
        }

        if (this.status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.SUSPENDED;
            return true;
        }

        return false;
    }

    public BigDecimal consumeInvoiceAmount() {
        BigDecimal invoiceAmount = this.amount;

        if (this.proratedChargeAmount != null) {
            invoiceAmount = invoiceAmount.add(this.proratedChargeAmount);
            this.proratedChargeAmount = null;
        }

        return invoiceAmount;
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

    public boolean isCancellationScheduled() {
        return cancellationScheduled;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
