package br.ifsp.demo.application.service;

import br.ifsp.demo.model.BillingCycle;
import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.SubscriptionRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
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

    @Test
    void shouldCreateSubscriptionWithBasicMonthlyPlanAndActiveStatus() {
        UUID customerId = UUID.randomUUID();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

        CreateSubscriptionService service = createService(userRepository, subscriptionRepository);

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
    void shouldThrowExceptionWhenUserIsNotRegistered() {
        UUID customerId = UUID.randomUUID();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(customerId)).thenReturn(Optional.empty());

        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

        CreateSubscriptionService service = createService(userRepository, subscriptionRepository);

        assertThatThrownBy(() -> service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldDefineInitialBillingPeriodForProMonthlySubscription() {
        UUID customerId = UUID.randomUUID();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

        CreateSubscriptionService service = createService(userRepository, subscriptionRepository);

        Subscription subscription = service.create(customerId, PlanType.PRO, BillingCycle.MONTHLY);

        assertThat(subscription.getBillingPeriod()).isNotNull();
        assertThat(subscription.getBillingPeriod().getStartDate()).isEqualTo(fixedDate);
        assertThat(subscription.getBillingPeriod().getEndDate())
                .isAfter(subscription.getBillingPeriod().getStartDate());

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).save(subscription);
    }

    private CreateSubscriptionService createService(JpaUserRepository userRepository,
                                                    SubscriptionRepository subscriptionRepository) {
        return new CreateSubscriptionService(userRepository, subscriptionRepository, fixedClock);
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

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyHasAnActiveSubscription() {
        UUID customerId = UUID.randomUUID();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(customerId)).thenReturn(Optional.of(buildUser(customerId)));

        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        when(subscriptionRepository.existsActiveByCustomerId(customerId)).thenReturn(true);

        CreateSubscriptionService service = createService(userRepository, subscriptionRepository);

        assertThatThrownBy(() -> service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer already has an active subscription");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository).existsActiveByCustomerId(customerId);
        verify(subscriptionRepository, never()).save(any());
    }
}