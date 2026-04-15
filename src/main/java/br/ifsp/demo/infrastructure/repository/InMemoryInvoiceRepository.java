package br.ifsp.demo.infrastructure.repository;

import br.ifsp.demo.model.BillingPeriod;
import br.ifsp.demo.model.Invoice;
import br.ifsp.demo.repository.InvoiceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final List<Invoice> invoices = new CopyOnWriteArrayList<>();

    @Override
    public void save(Invoice invoice) {
        invoices.add(invoice);
    }

    @Override
    public boolean existsBySubscriptionIdAndPeriod(UUID subscriptionId, BillingPeriod period) {
        return invoices.stream()
                .anyMatch(invoice ->
                        invoice.getSubscriptionId().equals(subscriptionId)
                                && invoice.getPeriod().getStartDate().equals(period.getStartDate())
                                && invoice.getPeriod().getEndDate().equals(period.getEndDate())
                );
    }
}
