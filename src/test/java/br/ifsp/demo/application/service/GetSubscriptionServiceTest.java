package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private GetSubscriptionService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        sut = new GetSubscriptionService(subscriptionRepository);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnCancelledStatusWhenCancelledSubscriptionIsQueried() {
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

        Subscription foundSubscription = sut.getById(subscriptionId);

        assertThat(foundSubscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Mutation")
    void shouldThrowErrorWhenGettingNonexistentSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getById(subscriptionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subscription not found");
    }
}