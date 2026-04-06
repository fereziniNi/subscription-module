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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
class CreateSubscriptionServiceTest {

    @Test
    void shouldCreateSubscriptionWithBasicMonthlyPlanAndActiveStatus() {
        UUID customerId = UUID.randomUUID();

        User user = User.builder()
                .id(customerId)
                .name("John")
                .lastname("Snow")
                .email("john.snow@email.com")
                .password("123456")
                .role(Role.USER)
                .build();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(customerId)).thenReturn(Optional.of(user));

        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

        CreateSubscriptionService service = new CreateSubscriptionService(userRepository, subscriptionRepository);

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

        CreateSubscriptionService service = new CreateSubscriptionService(userRepository, subscriptionRepository);

        assertThatThrownBy(() -> service.create(customerId, PlanType.BASIC, BillingCycle.MONTHLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer does not exist");

        verify(userRepository).findById(customerId);
        verify(subscriptionRepository, never()).save(any());
    }


}