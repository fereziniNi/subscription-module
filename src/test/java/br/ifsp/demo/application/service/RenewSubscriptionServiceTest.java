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


public class RenewSubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private RenewSubscriptionService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-05-02T00:00:00Z"),
                ZoneId.of("UTC")
        );
        sut = new RenewSubscriptionService(subscriptionRepository, fixedClock);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldRenewMonthlySubscriptionWithApprovedPaymentWhenPeriodHasExpired() {
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

        Subscription renewedSubscription = sut.renew(subscriptionId, true);

        assertThat(renewedSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldRenewYearlySubscriptionWithApprovedPaymentAndApplyAnnualDiscount() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 5, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription renewedSubscription = sut.renew(subscriptionId, true);

        assertThat(renewedSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2027, 5, 1));
        assertThat(renewedSubscription.getAmount()).isEqualByComparingTo("359.28");
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldSuspendSubscriptionAndKeepSamePeriodWhenRenewalPaymentIsRejected() {
        UUID subscriptionId = UUID.randomUUID();

        BillingPeriod expiredPeriod = new BillingPeriod(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 5, 1)
        );

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                expiredPeriod
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription renewedSubscription = sut.renew(subscriptionId, false);

        assertThat(renewedSubscription.getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 4, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenRenewalIsRequestedBeforePeriodExpiration() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Early renewal");

        verify(subscriptionRepository, never()).save(any());
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenRenewingCancelledSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled subscription");

        verify(subscriptionRepository, never()).save(any());
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldStartNewCycleWithScheduledPlanWhenRenewalIsApproved() {
        UUID subscriptionId = UUID.randomUUID();
        LocalDate currentDate = LocalDate.of(2026, 5, 2);

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        subscription.changePlan(PlanType.PLUS, currentDate);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription renewedSubscription = sut.renew(subscriptionId, true);

        assertThat(renewedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(renewedSubscription.getScheduledPlanType()).isNull();
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldKeepCurrentPlanWhenRenewalIsApprovedAndNoScheduledChangeExists() {
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

        Subscription renewedSubscription = sut.renew(subscriptionId, true);

        assertThat(renewedSubscription.getPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(renewedSubscription.getScheduledPlanType()).isNull();
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenRenewingSuspendedSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Suspended subscription");

        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenRenewingPreviouslyCancelledSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled subscription");

        verify(subscriptionRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
            "CANCELLED, Cancelled subscription",
            "SUSPENDED, Suspended subscription"
    })
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldBlockRenewalForInvalidSubscriptionStatuses(
            SubscriptionStatus status,
            String expectedMessage
    ) {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                status,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(expectedMessage);

        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldBlockRenewalWhenCurrentDateIsExactlyThePeriodEndDate() {
        subscriptionRepository = mock(SubscriptionRepository.class);

        Clock boundaryClock = Clock.fixed(
                Instant.parse("2026-05-01T00:00:00Z"),
                ZoneId.of("UTC")
        );

        sut = new RenewSubscriptionService(subscriptionRepository, boundaryClock);

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

        assertThatThrownBy(() -> sut.renew(subscriptionId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Early renewal");

        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldAllowRenewalWhenCurrentDateIsTheDayAfterPeriodEndDate() {
        subscriptionRepository = mock(SubscriptionRepository.class);

        Clock boundaryClock = Clock.fixed(
                Instant.parse("2026-05-02T00:00:00Z"),
                ZoneId.of("UTC")
        );

        sut = new RenewSubscriptionService(subscriptionRepository, boundaryClock);

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

        Subscription renewedSubscription = sut.renew(subscriptionId, true);

        assertThat(renewedSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(renewedSubscription.getBillingPeriod().getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(renewedSubscription.getBillingPeriod().getEndDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        verify(subscriptionRepository).save(subscription);
    }

}
