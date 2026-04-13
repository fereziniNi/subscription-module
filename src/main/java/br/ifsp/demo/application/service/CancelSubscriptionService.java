package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CancelSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public CancelSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription cancelImmediately(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.cancelImmediately();
        subscriptionRepository.save(subscription);

        return subscription;
    }

    public Subscription cancelAtPeriodEnd(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.cancelAtPeriodEnd();
        subscriptionRepository.save(subscription);

        return subscription;
    }

    public Subscription reverseScheduledCancellation(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.reverseScheduledCancellation();
        subscriptionRepository.save(subscription);

        return subscription;
    }
}