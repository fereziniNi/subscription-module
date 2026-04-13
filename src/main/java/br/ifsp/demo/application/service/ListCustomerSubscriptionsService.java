package br.ifsp.demo.application.service;

import br.ifsp.demo.application.gateway.CustomerAccountGateway;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListCustomerSubscriptionsService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerAccountGateway customerAccountGateway;

    public ListCustomerSubscriptionsService(
            SubscriptionRepository subscriptionRepository,
            CustomerAccountGateway customerAccountGateway
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerAccountGateway = customerAccountGateway;
    }

    public List<Subscription> findByCustomerId(UUID customerId) {
        if (!customerAccountGateway.existsById(customerId)) {
            throw new IllegalArgumentException("Customer does not exist");
        }

        return subscriptionRepository.findByCustomerId(customerId);
    }

    public List<Subscription> findByCustomerIdAndStatus(UUID customerId, SubscriptionStatus status) {
        if (!customerAccountGateway.existsById(customerId)) {
            throw new IllegalArgumentException("Customer does not exist");
        }

        return subscriptionRepository.findByCustomerId(customerId).stream()
                .filter(subscription -> subscription.getStatus() == status)
                .toList();
    }
}