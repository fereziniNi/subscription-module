package br.ifsp.demo.controller.dto;

import br.ifsp.demo.model.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        UUID subscriptionId,
        BigDecimal amount,
        LocalDate periodStartDate,
        LocalDate periodEndDate
) {

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getSubscriptionId(),
                invoice.getAmount(),
                invoice.getPeriod().getStartDate(),
                invoice.getPeriod().getEndDate()
        );
    }
}
