package br.ifsp.demo.application.service;

import br.ifsp.demo.model.PlanType;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;


@Service
public class ChangeSubscriptionPlanService {
    private final SubscriptionRepository subscriptionRepository;

    public ChangeSubscriptionPlanService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription changePlan(java.util.UUID subscriptionId, PlanType newPlanType) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.changePlan(newPlanType);
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
