package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

public class CancelSubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private CancelSubscriptionService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-05-02T00:00:00Z"),
                ZoneId.of("UTC")
        );
        sut = new CancelSubscriptionService(subscriptionRepository);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldCancelSubscriptionImmediatelyWhenItIsActive() {
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

        Subscription cancelledSubscription = sut.cancelImmediately(subscriptionId);

        assertThat(cancelledSubscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldScheduleCancellationAndKeepSubscriptionActiveUntilEndOfCurrentPeriod() {
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

        Subscription updatedSubscription = sut.cancelAtPeriodEnd(subscriptionId);

        assertThat(updatedSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(updatedSubscription.isCancellationScheduled()).isTrue();
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldRemoveScheduledCancellationAndKeepSubscriptionActiveWhenReversingCancellation() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        subscription.cancelAtPeriodEnd();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.reverseScheduledCancellation(subscriptionId);

        assertThat(updatedSubscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(updatedSubscription.isCancellationScheduled()).isFalse();
        verify(subscriptionRepository).save(subscription);
    }
}
