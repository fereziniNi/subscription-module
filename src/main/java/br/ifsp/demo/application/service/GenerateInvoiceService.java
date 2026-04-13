package br.ifsp.demo.application.service;

import br.ifsp.demo.model.Invoice;
import br.ifsp.demo.model.Subscription;
import br.ifsp.demo.model.SubscriptionStatus;
import br.ifsp.demo.repository.InvoiceRepository;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GenerateInvoiceService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    public GenerateInvoiceService(SubscriptionRepository subscriptionRepository, InvoiceRepository invoiceRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice generate(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (invoiceRepository.existsBySubscriptionIdAndPeriod(subscriptionId, subscription.getBillingPeriod())) {
            throw new IllegalStateException("Duplicate invoice");
        }

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled subscription");
        }

        Invoice invoice = new Invoice(
                subscriptionId,
                subscription.getBillingPeriod(),
                subscription.getAmount()
        );

        invoiceRepository.save(invoice);
        return invoice;
    }
}