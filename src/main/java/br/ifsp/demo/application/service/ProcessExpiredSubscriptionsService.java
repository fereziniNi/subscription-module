package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class ProcessExpiredSubscriptionsService {

    private final SubscriptionRepository subscriptionRepository;
    private final Clock clock;

    public ProcessExpiredSubscriptionsService(SubscriptionRepository subscriptionRepository, Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.clock = clock;
    }

    public void processExpiredSubscriptions() {
        LocalDate currentDate = LocalDate.now(clock);

        for (Subscription subscription : subscriptionRepository.findAll()) {
            if (subscription.processExpiration(currentDate)) {
                subscriptionRepository.save(subscription);
            }
        }
    }
}
