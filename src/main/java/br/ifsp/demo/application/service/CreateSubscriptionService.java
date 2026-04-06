package br.ifsp.demo.application.service;

import br.ifsp.demo.repository.SubscriptionRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import org.springframework.stereotype.Service;
import br.ifsp.demo.model.BillingCycle;
import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;

import java.math.BigDecimal;
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

        Subscription subscription = new Subscription(
                customerId,
                planType,
                billingCycle,
                SubscriptionStatus.ACTIVE,
                amount
        );

        subscriptionRepository.save(subscription);

        return subscription;
    }
}
