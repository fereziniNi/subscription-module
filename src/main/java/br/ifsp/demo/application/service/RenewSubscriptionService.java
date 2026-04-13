package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class RenewSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    public RenewSubscriptionService(SubscriptionRepository subscriptionRepository, Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
    }

    public Subscription renew(UUID subscriptionId, boolean paymentApproved) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.renew(paymentApproved, LocalDate.now(clock));
        subscriptionRepository.save(subscription);

        return subscription;
    }

}