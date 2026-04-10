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

import static org.mockito.Mockito.*;


public class RenewSubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private ChangeSubscriptionPlanService sut;

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
}
