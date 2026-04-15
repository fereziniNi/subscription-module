package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ChangeSubscriptionPlanServiceTest {


    private SubscriptionRepository subscriptionRepository;
    private ChangeSubscriptionPlanService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-16T00:00:00Z"),
                ZoneId.of("UTC")
        );

        sut = new ChangeSubscriptionPlanService(subscriptionRepository, fixedClock);

    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangeCurrentPlanImmediatelyWhenSubscriptionIsActiveAndUpgradeFromBasicToPlusIsRequested() {

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangeCurrentPlanImmediatelyWhenSubscriptionIsActiveAndUpgradeFromPlusToProIsRequested() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PRO);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldKeepCurrentPlanAndScheduleNewPlanForNextCycleWhenSubscriptionIsActiveAndDowngradeFromProToPlusIsRequested() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(PlanType.PLUS);
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldKeepCurrentPlanAndScheduleNewPlanForNextCycleWhenSubscriptionIsActiveAndDowngradeFromPlusToBasicIsRequested() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.BASIC);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(PlanType.BASIC);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenRequestedPlanIsTheSameAsCurrentPlan() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.changePlan(subscriptionId, PlanType.PLUS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Plan already contracted");

        verify(subscriptionRepository, never()).save(any());
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldRemoveScheduledDowngradeAndChangeCurrentPlanImmediatelyWhenUpgradeToProIsRequested() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        subscription.changePlan(PlanType.BASIC, LocalDate.now());

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PRO);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(updatedSubscription.getScheduledPlanType()).isNull();
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenChangingPlanOfCancelledSubscription() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.changePlan(subscriptionId, PlanType.PRO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Inactive subscription");

        verify(subscriptionRepository, never()).save(any());
    }


    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenChangingPlanOfSuspendedSubscription() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.changePlan(subscriptionId, PlanType.PRO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Suspended subscription");

        verify(subscriptionRepository, never()).save(any());
    }


    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangePlanImmediatelyAndCalculateProratedChargeWhenUpgradingFromBasicToPlus() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(updatedSubscription.getProratedChargeAmount()).isEqualByComparingTo("10.00");
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangePlanImmediatelyAndCalculateProratedChargeWhenUpgradingFromPlusToPro() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldScheduleDowngradeWithoutImmediateChargeWhenChangingFromProToPlus() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(updatedSubscription.getProratedChargeAmount()).isNull();
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldScheduleDowngradeWithoutImmediateChargeWhenChangingFromPlusToBasic() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.BASIC);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(updatedSubscription.getProratedChargeAmount()).isNull();
        verify(subscriptionRepository).save(subscription);
    }

    // Functional tests

    @ParameterizedTest
    @CsvSource(value = {
            "BASIC, PRO, PRO, , false",
            "PRO, BASIC, PRO, BASIC, true"
    }, nullValues = "")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldHandleEquivalentPlanChangeCases(
            PlanType currentPlan,
            PlanType requestedPlan,
            PlanType expectedCurrentPlan,
            PlanType expectedScheduledPlan,
            boolean shouldHaveScheduledPlan
    ) {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                currentPlan,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                currentPlan.getMonthlyPrice(),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, requestedPlan);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(expectedCurrentPlan);

        if (shouldHaveScheduledPlan) {
            assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(expectedScheduledPlan);
        } else {
            assertThat(updatedSubscription.getScheduledPlanType()).isNull();
        }

        verify(subscriptionRepository).save(subscription);
    }

    @ParameterizedTest
    @CsvSource({
            "2026-04-30T00:00:00Z, 0.67",
            "2026-04-29T00:00:00Z, 1.33"
    })
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldCalculateProratedChargeNearCycleEnd(
            String instantText,
            BigDecimal expectedProratedAmount
    ) {
        subscriptionRepository = mock(SubscriptionRepository.class);

        Clock dynamicClock = Clock.fixed(
                Instant.parse(instantText),
                ZoneId.of("UTC")
        );

        sut = new ChangeSubscriptionPlanService(subscriptionRepository, dynamicClock);

        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(updatedSubscription.getProratedChargeAmount()).isEqualByComparingTo(expectedProratedAmount);
        verify(subscriptionRepository).save(subscription);
    }


}