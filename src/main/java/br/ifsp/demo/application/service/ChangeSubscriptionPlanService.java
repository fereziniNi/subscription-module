package br.ifsp.demo.application.service;

import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;


@Service
public class ChangeSubscriptionPlanService {
    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    public ChangeSubscriptionPlanService(SubscriptionRepository subscriptionRepository, Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
    }

    public Subscription changePlan(java.util.UUID subscriptionId, PlanType newPlanType) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.changePlan(newPlanType, LocalDate.now(clock));
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
