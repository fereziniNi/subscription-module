package br.ifsp.demo.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Structural")
class InvoiceTest {

    @Test
    void shouldReturnConstructorValuesThroughGetters() {
        UUID subscriptionId = UUID.randomUUID();
        BillingPeriod period = new BillingPeriod(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1)
        );
        BigDecimal amount = new BigDecimal("29.90");

        Invoice invoice = new Invoice(subscriptionId, period, amount);

        assertThat(invoice.getId()).isNotNull();
        assertThat(invoice.getSubscriptionId()).isEqualTo(subscriptionId);
        assertThat(invoice.getPeriod()).isEqualTo(period);
        assertThat(invoice.getAmount()).isEqualByComparingTo(amount);
    }
}