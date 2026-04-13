package br.ifsp.demo.application.service;

import br.ifsp.demo.application.gateway.CustomerAccountGateway;
import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListCustomerSubscriptionsServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private CustomerAccountGateway customerAccountGateway;
    private ListCustomerSubscriptionsService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        customerAccountGateway = mock(CustomerAccountGateway.class);
        sut = new ListCustomerSubscriptionsService(subscriptionRepository, customerAccountGateway);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnOnlySubscriptionsBelongingToCustomer() {
        UUID customerId = UUID.randomUUID();

        Subscription firstSubscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        Subscription secondSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(firstSubscription, secondSubscription));

        List<Subscription> subscriptions = sut.findByCustomerId(customerId);

        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions).containsExactly(firstSubscription, secondSubscription);
    }
}