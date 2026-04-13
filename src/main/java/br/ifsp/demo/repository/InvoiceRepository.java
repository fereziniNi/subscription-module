package br.ifsp.demo.repository;

import br.ifsp.demo.model.BillingPeriod;
import br.ifsp.demo.model.Invoice;

import java.util.UUID;

public interface InvoiceRepository {
    void save(Invoice invoice);
    boolean existsBySubscriptionIdAndPeriod(UUID subscriptionId, BillingPeriod period);
}