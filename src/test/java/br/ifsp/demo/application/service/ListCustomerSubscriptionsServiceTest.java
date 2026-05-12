package br.ifsp.demo.application.service;

import br.ifsp.demo.application.gateway.CustomerAccountGateway;
import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnEmptyListWhenCustomerHasNoSubscriptions() {
        UUID customerId = UUID.randomUUID();

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Subscription> subscriptions = sut.findByCustomerId(customerId);

        assertThat(subscriptions).isEmpty();
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnSubscriptionsWithCorrectStatusPlanAndBillingCycle() {
        UUID customerId = UUID.randomUUID();

        Subscription activeSubscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        Subscription cancelledSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1))
        );

        Subscription suspendedSubscription = new Subscription(
                customerId,
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(activeSubscription, cancelledSubscription, suspendedSubscription));

        List<Subscription> subscriptions = sut.findByCustomerId(customerId);

        assertThat(subscriptions).hasSize(3);

        assertThat(subscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscriptions.get(0).getPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(subscriptions.get(0).getBillingCycle()).isEqualTo(BillingCycle.MONTHLY);

        assertThat(subscriptions.get(1).getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(subscriptions.get(1).getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(subscriptions.get(1).getBillingCycle()).isEqualTo(BillingCycle.YEARLY);

        assertThat(subscriptions.get(2).getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
        assertThat(subscriptions.get(2).getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(subscriptions.get(2).getBillingCycle()).isEqualTo(BillingCycle.MONTHLY);
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        when(customerAccountGateway.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> sut.findByCustomerId(customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(subscriptionRepository, never()).findByCustomerId(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnOnlyActiveSubscriptionsWhenFilteringByActiveStatus() {
        UUID customerId = UUID.randomUUID();

        Subscription activeSubscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        Subscription cancelledSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1))
        );

        Subscription suspendedSubscription = new Subscription(
                customerId,
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(activeSubscription, cancelledSubscription, suspendedSubscription));

        List<Subscription> subscriptions = sut.findByCustomerIdAndStatus(customerId, SubscriptionStatus.ACTIVE);

        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscriptions.get(0)).isEqualTo(activeSubscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnSubscriptionsOrderedByCreationDateFromNewestToOldest() {
        UUID customerId = UUID.randomUUID();

        Subscription olderSubscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1)),
                LocalDate.of(2026, 4, 1)
        );

        Subscription newerSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1)),
                LocalDate.of(2026, 5, 1)
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(olderSubscription, newerSubscription));

        List<Subscription> subscriptions = sut.findByCustomerIdOrderByCreatedAtDesc(customerId);

        assertThat(subscriptions).containsExactly(newerSubscription, olderSubscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldKeepCancelledSubscriptionVisibleInCustomerHistory() {
        UUID customerId = UUID.randomUUID();

        Subscription cancelledSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(cancelledSubscription));

        List<Subscription> subscriptions = sut.findByCustomerId(customerId);

        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(subscriptions).containsExactly(cancelledSubscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldReturnSuspendedSubscriptionWithItsLastContractedPlan() {
        UUID customerId = UUID.randomUUID();

        Subscription suspendedSubscription = new Subscription(
                customerId,
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(suspendedSubscription));

        List<Subscription> subscriptions = sut.findByCustomerId(customerId);

        assertThat(subscriptions).hasSize(1);
        assertThat(subscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
        assertThat(subscriptions.get(0).getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(subscriptions).containsExactly(suspendedSubscription);
    }

    @ParameterizedTest
    @CsvSource({
            "ACTIVE",
            "CANCELLED",
            "SUSPENDED"
    })
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldReturnOnlySubscriptionsMatchingRequestedStatusFilter(SubscriptionStatus requestedStatus) {
        UUID customerId = UUID.randomUUID();

        Subscription activeSubscription = new Subscription(
                customerId,
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        Subscription cancelledSubscription = new Subscription(
                customerId,
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1))
        );

        Subscription suspendedSubscription = new Subscription(
                customerId,
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        when(customerAccountGateway.existsById(customerId)).thenReturn(true);
        when(subscriptionRepository.findByCustomerId(customerId))
                .thenReturn(List.of(activeSubscription, cancelledSubscription, suspendedSubscription));

        List<Subscription> subscriptions = sut.findByCustomerIdAndStatus(customerId, requestedStatus);

        assertThat(subscriptions).allMatch(subscription -> subscription.getStatus() == requestedStatus);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    void shouldThrowErrorWhenFilteringSubscriptionsByStatusForNonexistentCustomer() {
        UUID customerId = UUID.randomUUID();

        when(customerAccountGateway.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> sut.findByCustomerIdAndStatus(customerId, SubscriptionStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(subscriptionRepository, never()).findByCustomerId(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    void shouldThrowErrorWhenOrderingSubscriptionsByCreationDateForNonexistentCustomer() {
        UUID customerId = UUID.randomUUID();

        when(customerAccountGateway.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> sut.findByCustomerIdOrderByCreatedAtDesc(customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(subscriptionRepository, never()).findByCustomerId(any());
    }


}