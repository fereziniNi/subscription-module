package br.ifsp.demo.application.service;

import br.ifsp.demo.model.BillingCycle;
import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.SubscriptionRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
class CreateSubscriptionServiceTest {

    private final LocalDate fixedDate = LocalDate.of(2026, 4, 6);
    private final Clock fixedClock = Clock.fixed(
            fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
    );

    private JpaUserRepository userRepository;
    private SubscriptionRepository subscriptionRepository;
    private CreateSubscriptionService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(JpaUserRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        service = new CreateSubscriptionService(userRepository, subscriptionRepository, fixedClock);
    }

    @Test
    void shouldCreateSubscriptionWithBasicMonthlyPlanAndActiveStatus() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        Subscription subscription = service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY);

        assertThat(subscription.getId()).isNotNull();
        assertThat(subscription.getCustomerId()).isEqualTo(customerId);
        assertThat(subscription.getBillingPeriod()).isNotNull();
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getPlanType()).isEqualTo(PlanType.BASIC);
        assertThat(subscription.getBillingCycle()).isEqualTo(BillingCycle.MONTHLY);
        assertThat(subscription.getAmount()).isEqualByComparingTo("29.90");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldCreateSubscriptionWithPlusYearlyPlanAndAnnualDiscount() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        Subscription subscription = service.create(customerId, PlanType.PLUS, BillingCycle.YEARLY);

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getPlanType()).isEqualTo(PlanType.PLUS);
        assertThat(subscription.getBillingCycle()).isEqualTo(BillingCycle.YEARLY);
        assertThat(subscription.getAmount()).isEqualByComparingTo("359.28");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldDefineInitialBillingPeriodForProMonthlySubscription() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        Subscription subscription = service.create(customerId, PlanType.PRO, BillingCycle.MONTHLY);

        assertThat(subscription.getBillingPeriod()).isNotNull();
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(fixedDate);
        assertThat(subscription.getBillingPeriod().getEndDate())
                .isAfter(subscription.getBillingPeriod().getStartDate());

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldProvideUniqueIdentifierWhenSubscriptionIsCreatedSuccessfully() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        Subscription subscription = service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY);

        assertThat(subscription.getId()).isNotNull();

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotRegistered() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyHasAnActiveSubscription() {
        UUID customerId = UUID.randomUUID();
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));
        when(subscriptionRepository.existsActiveByCustomerId(customerId)).thenReturn(true);

        assertThatThrownBy(() -> service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer already has an active subscription");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).existsActiveByCustomerId(customerId);
        verify(subscriptionRepository, never()).save(any());
    }

    private User buildUser(UUID customerId) {
        return User.builder()
                .id(customerId)
                .name("John")
                .lastname("Snow")
                .email("john.snow@email.com")
                .password("123456")
                .role(Role.USER)
                .build();
    }
}