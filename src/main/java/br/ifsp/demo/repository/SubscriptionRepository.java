package br.ifsp.demo.repository;

import br.ifsp.demo.model.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {
    void save(Subscription subscription);
    boolean existsActiveByCustomerId(UUID customerId);
    Optional<Subscription> findById(UUID subscriptionId);
    List<Subscription> findByCustomerId(UUID customerId);
    List<Subscription> findAll();

}
