package br.ifsp.demo.repository;

import br.ifsp.demo.model.Subscription;

import java.util.UUID;

public interface SubscriptionRepository {
    void save(Subscription subscription);
    boolean existsActiveByCustomerId(UUID customerId);

}
