package br.ifsp.demo.application.service;

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

    public CreateSubscriptionService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Subscription create(UUID customerId, PlanType planType, BillingCycle billingCycle) {
        if (userRepository.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException("Customer does not exist");
        }


        return new Subscription(customerId, planType, billingCycle, SubscriptionStatus.ACTIVE,new BigDecimal("29.90"));
    }
}
