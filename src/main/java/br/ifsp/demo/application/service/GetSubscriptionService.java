package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public GetSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription getById(UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
    }
}