package br.ifsp.demo.application.service;

import br.ifsp.demo.model.*;
import br.ifsp.demo.repository.InvoiceRepository;
import br.ifsp.demo.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GenerateInvoiceServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private InvoiceRepository invoiceRepository;
    private GenerateInvoiceService sut;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        invoiceRepository = mock(InvoiceRepository.class);
        sut = new GenerateInvoiceService(subscriptionRepository, invoiceRepository);
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldGenerateMonthlyInvoiceWithBasicPlanPrice() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndPeriod(subscriptionId, subscription.getBillingPeriod())).thenReturn(false);

        Invoice generatedInvoice = sut.generate(subscriptionId);

        assertThat(generatedInvoice.getAmount()).isEqualByComparingTo("29.90");
        verify(invoiceRepository).save(any(Invoice.class));
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldGenerateYearlyInvoiceWithAnnualDiscountForPlusPlan() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.PLUS,
                BillingCycle.YEARLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("359.28"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2027, 6, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndPeriod(subscriptionId, subscription.getBillingPeriod())).thenReturn(false);

        Invoice generatedInvoice = sut.generate(subscriptionId);

        assertThat(generatedInvoice.getAmount()).isEqualByComparingTo("359.28");
        verify(invoiceRepository).save(any(Invoice.class));
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldGenerateInvoiceWithOwnIdentifierAndMatchingBillingPeriod() {
        UUID subscriptionId = UUID.randomUUID();

        BillingPeriod billingPeriod = new BillingPeriod(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1)
        );

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                billingPeriod
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndPeriod(subscriptionId, billingPeriod)).thenReturn(false);

        Invoice generatedInvoice = sut.generate(subscriptionId);

        assertThat(generatedInvoice.getId()).isNotNull();
        assertThat(generatedInvoice.getPeriod()).isEqualTo(billingPeriod);
        verify(invoiceRepository).save(any(Invoice.class));
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenGeneratingDuplicateInvoiceForCurrentPeriod() {
        UUID subscriptionId = UUID.randomUUID();

        BillingPeriod billingPeriod = new BillingPeriod(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1)
        );

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.ACTIVE,
                new BigDecimal("29.90"),
                billingPeriod
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(invoiceRepository.existsBySubscriptionIdAndPeriod(subscriptionId, billingPeriod)).thenReturn(true);

        assertThatThrownBy(() -> sut.generate(subscriptionId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Duplicate invoice");

        verify(invoiceRepository, never()).save(any());
    }
    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenGeneratingInvoiceForCancelledSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.CANCELLED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.generate(subscriptionId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled subscription");

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    void shouldThrowErrorWhenGeneratingInvoiceForSuspendedSubscription() {
        UUID subscriptionId = UUID.randomUUID();

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                PlanType.BASIC,
                BillingCycle.MONTHLY,
                SubscriptionStatus.SUSPENDED,
                new BigDecimal("29.90"),
                new BillingPeriod(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1))
        );

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        assertThatThrownBy(() -> sut.generate(subscriptionId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Suspended subscription");

        verify(invoiceRepository, never()).save(any());
    }

}