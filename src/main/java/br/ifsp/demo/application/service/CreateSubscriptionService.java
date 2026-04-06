package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Service
public class CreateSubscriptionService {
    private final JpaUserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public CreateSubscriptionService(JpaUserRepository userRepository,SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription create(UUID customerId, PlanType planType, BillingCycle billingCycle) {
        if (userRepository.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException("Customer does not exist");
        }

        BigDecimal amount = planType.getMonthlyPrice();

        if (billingCycle == BillingCycle.YEARLY) {
            amount = planType.getMonthlyPrice()
                    .multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal("0.60"));
        }

        BillingPeriod billingPeriod = new BillingPeriod(LocalDate.now(), LocalDate.now().plusMonths(1));

        Subscription subscription = new Subscription(
                customerId,
                planType,
                billingCycle,
                SubscriptionStatus.ACTIVE,
                amount,
                billingPeriod
        );

        subscriptionRepository.save(subscription);

        return subscription;
    }
}
