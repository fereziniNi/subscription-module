package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChangeSubscriptionPlanServiceTest {

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangeCurrentPlanImmediatelyWhenSubscriptionIsActiveAndUpgradeFromBasicToPlusIsRequested() {
        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        ChangeSubscriptionPlanService sut = new ChangeSubscriptionPlanService(subscriptionRepository);

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PLUS);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldChangeCurrentPlanImmediatelyWhenSubscriptionIsActiveAndUpgradeFromPlusToProIsRequested() {
        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        ChangeSubscriptionPlanService sut = new ChangeSubscriptionPlanService(subscriptionRepository);

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("49.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PRO);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldKeepCurrentPlanAndScheduleNewPlanForNextCycleWhenSubscriptionIsActiveAndDowngradeFromProToPlusIsRequested() {
        SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
        ChangeSubscriptionPlanService sut = new ChangeSubscriptionPlanService(subscriptionRepository);

        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PRO,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("79.90"),
                new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        Subscription updatedSubscription = sut.changePlan(subscriptionId, PlanType.PLUS);

        assertThat(updatedSubscription.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(updatedSubscription.getScheduledPlanType()).isEqualTo(PlanType.PLUS);
        verify(subscriptionRepository).save(subscription);
    }




}