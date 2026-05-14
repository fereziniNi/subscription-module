package br.ifsp.demo.infrastructure.repository;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private final ConcurrentMap<UUID, Subscription> subscriptions = new ConcurrentHashMap<>();

    @Override
    public void save(Subscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
    }

    @Override
    public boolean existsActiveByCustomerId(UUID customerId) {
        return subscriptions.values().stream()
                .anyMatch(subscription ->
                        subscription.getCustomerId().equals(customerId)
                                && subscription.getStatus() == SubscriptionStatus.ACTIVE
                );
    }

    @Override
    public Optional<Subscription> findById(UUID subscriptionId) {
        return Optional.ofNullable(subscriptions.get(subscriptionId));
    }

    @Override
    public List<Subscription> findByCustomerId(UUID customerId) {
        return subscriptions.values().stream()
                .filter(subscription -> subscription.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Subscription> findAll() {
        return List.copyOf(subscriptions.values());
    }
}
