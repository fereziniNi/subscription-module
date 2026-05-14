package br.ifsp.demo.application.service;

import br.ifsp.demo.model.BillingCycle;
import br.ifsp.demo.model.BillingPeriod;
import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class ProcessExpiredSubscriptionsServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private ProcessExpiredSubscriptionsService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-06-01T00:00:00Z"),
                ZoneId.of("UTC")
        );
        sut = new ProcessExpiredSubscriptionsService(subscriptionRepository, fixedClock);
    }

    @Test
    void shouldKeepActiveSubscriptionOnBillingPeriodEndDate() {
        Subscription subscription = subscriptionEndingOn(LocalDate.of(2026, 6, 1));
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        sut.processExpiredSubscriptions();

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        verify(subscriptionRepository, never()).save(subscription);
    }

    @Test
    void shouldSuspendActiveSubscriptionOneDayAfterBillingPeriodEndDate() {
        Subscription subscription = subscriptionEndingOn(LocalDate.of(2026, 5, 31));
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        sut.processExpiredSubscriptions();

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldCancelExpiredSubscriptionWhenCancellationIsScheduled() {
        Subscription subscription = subscriptionEndingOn(LocalDate.of(2026, 5, 31));
        subscription.cancelAtPeriodEnd();
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        sut.processExpiredSubscriptions();

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldKeepActiveSubscriptionWhenBillingPeriodHasNotExpired() {
        Subscription subscription = subscriptionEndingOn(LocalDate.of(2026, 6, 2));
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        sut.processExpiredSubscriptions();

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        verify(subscriptionRepository, never()).save(subscription);
    }

    private Subscription subscriptionEndingOn(LocalDate endDate) {
        return new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), endDate)
        );
    }
}
