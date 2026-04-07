package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.SubscriptionRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Clock;
import java.time.LocalDate;


@Service
public class CreateSubscriptionService {
    private final JpaUserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final Clock clock;

    public CreateSubscriptionService(JpaUserRepository userRepository,SubscriptionRepository subscriptionRepository, Clock clock) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
    }

    public Subscription create(UUID customerId, PlanType planType, BillingCycle billingCycle) {
        if (userRepository.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException("Customer does not exist");
        }

        if (subscriptionRepository.existsActiveByCustomerId(customerId)) {
            throw new IllegalArgumentException("Customer already has an active subscription");
        }

        BigDecimal amount = planType.getMonthlyPrice();

        if (billingCycle == BillingCycle.YEARLY) {
            amount = planType.getMonthlyPrice()
                    .multiply(new BigDecimal("12"))
                    .multiply(new BigDecimal("0.60"));
        }

        LocalDate creationDate = LocalDate.now(clock);

        BillingPeriod billingPeriod = switch (billingCycle) {
            case MONTHLY -> new BillingPeriod(creationDate, creationDate.plusMonths(1));
            case YEARLY -> new BillingPeriod(creationDate, creationDate.plusYears(1));
        };

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
