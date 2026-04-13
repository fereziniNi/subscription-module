package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CancelSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    public CancelSubscriptionService(SubscriptionRepository subscriptionRepository, Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
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

    public Subscription processCycleEnding(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.processCycleEnding(LocalDate.now(clock));
        subscriptionRepository.save(subscription);

        return subscription;
    }
}