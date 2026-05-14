package br.ifsp.demo.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class SubscriptionTest {

    @Test
    void shouldApplyImmediateUpgradeWhenNewPlanIsHigher() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.changePlan(PlanType.PLUS, LocalDate.of(2026, 4, 16));

        assertThat(subscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(subscription.getScheduledPlanType()).isNull();
        assertThat(subscription.getProratedChargeAmount()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldScheduleDowngradeWhenNewPlanIsLower() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.changePlan(PlanType.BASIC, LocalDate.of(2026, 4, 16));

        assertThat(subscription.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(subscription.getScheduledPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(subscription.getProratedChargeAmount()).isNull();
    }

    @Test
    void shouldThrowErrorWhenChangingToSamePlan() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        assertThatThrownBy(() -> subscription.changePlan(PlanType.PLUS, LocalDate.of(2026, 4, 16)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Plan already contracted");
    }

    @Test
    void shouldThrowErrorWhenChangingPlanOfCancelledSubscription() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        assertThatThrownBy(() -> subscription.changePlan(PlanType.PRO, LocalDate.of(2026, 4, 16)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Inactive subscription");
    }

    @Test
    void shouldThrowErrorWhenChangingPlanOfSuspendedSubscription() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        assertThatThrownBy(() -> subscription.changePlan(PlanType.PRO, LocalDate.of(2026, 4, 16)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Suspended subscription");
    }

    @Test
    void shouldRenewMonthlySubscriptionWithApprovedPayment() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.renew(true, LocalDate.of(2026, 5, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 2));
        assertThat(subscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 2));
        assertThat(subscription.getAmount()).isEqualByComparingTo("29.90");
    }

    @Test
    void shouldRenewYearlySubscriptionWithApprovedPayment() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 5, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.renew(true, LocalDate.of(2026, 5, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 2));
        assertThat(subscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2027, 5, 2));
        assertThat(subscription.getAmount()).isEqualByComparingTo("359.28");
    }

    @Test
    void shouldSuspendSubscriptionWhenRenewalPaymentIsRejected() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.renew(false, LocalDate.of(2026, 5, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 4, 1));
        assertThat(subscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 5, 1));
    }

    @Test
    void shouldThrowErrorWhenRenewingCancelledSubscription() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        assertThatThrownBy(() -> subscription.renew(true, LocalDate.of(2026, 5, 2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled subscription");
    }

    @Test
    void shouldReactivateSuspendedSubscriptionWhenRenewingWithApprovedPayment() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.renew(true, LocalDate.of(2026, 5, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 2));
        assertThat(subscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 2));
    }

    @Test
    void shouldThrowErrorWhenRenewalIsRequestedEarly() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        assertThatThrownBy(() -> subscription.renew(true, LocalDate.of(2026, 5, 2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Early renewal");
    }

    @Test
    void shouldApplyScheduledPlanWhenRenewingApprovedSubscription() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.changePlan(PlanType.BASIC, LocalDate.of(2026, 4, 16));
        subscription.renew(true, LocalDate.of(2026, 5, 2));

        assertThat(subscription.getPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(subscription.getScheduledPlanType()).isNull();
        assertThat(subscription.getAmount()).isEqualByComparingTo("29.90");
    }

    @Test
    void shouldCancelImmediatelyWhenSubscriptionIsActive() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelImmediately();

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void shouldThrowErrorWhenCancellingAlreadyCancelledSubscription() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        assertThatThrownBy(subscription::cancelImmediately)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled subscription");
    }

    @Test
    void shouldScheduleCancellationAtPeriodEnd() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelAtPeriodEnd();

        assertThat(subscription.isCancellationScheduled()).isTrue();
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void shouldReverseScheduledCancellation() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelAtPeriodEnd();
        subscription.reverseScheduledCancellation();

        assertThat(subscription.isCancellationScheduled()).isFalse();
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void shouldCancelSubscriptionWhenCycleEndingAfterScheduledCancellation() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelAtPeriodEnd();
        subscription.processCycleEnding(LocalDate.of(2026, 6, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    void shouldSuspendSubscriptionWhenCycleEndingOneDayAfterPeriodEnd() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.processCycleEnding(LocalDate.of(2026, 6, 2));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
    }

    @Test
    void shouldKeepSubscriptionActiveWhenCycleEndingOnPeriodEndDate() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.processCycleEnding(LocalDate.of(2026, 6, 1));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void shouldKeepSubscriptionActiveWhenCycleEndingBeforePeriodExpiration() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelAtPeriodEnd();
        subscription.processCycleEnding(LocalDate.of(2026, 5, 31));

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void shouldReturnConstructorValuesThroughGetters() {
        UUID customerId = java.util.UUID.randomUUID();
        LocalDate createdAt = LocalDate.of(2026, 4, 1);

        Subscription subscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1)),
                createdAt
        );

        assertThat(subscription.getId()).isNotNull();
        assertThat(subscription.getCustomerId()).isEqualTo(customerId);
        assertThat(subscription.getBillingCycle()).isEqualTo(BillingCycle.MONTHLY);
        assertThat(subscription.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldConsumeProratedChargeInInvoiceAmountOnlyOnce() {
        Subscription subscription = new Subscription(
                java.util.UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.changePlan(PlanType.PLUS, LocalDate.of(2026, 4, 16));

        assertThat(subscription.consumeInvoiceAmount()).isEqualByComparingTo("39.90");
        assertThat(subscription.getProratedChargeAmount()).isNull();
        assertThat(subscription.consumeInvoiceAmount()).isEqualByComparingTo("29.90");
    }
}
