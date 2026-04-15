package br.ifsp.demo.controller.dto;

import br.ifsp.demo.model.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID customerId,
        String planType,
        String billingCycle,
        String status,
        BigDecimal amount,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        String scheduledPlanType,
        BigDecimal proratedChargeAmount,
        boolean cancellationScheduled,
        LocalDate createdAt
) {

    public static SubscriptionResponse from(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getCustomerId(),
                subscription.getPlanType().name(),
                subscription.getBillingCycle().name(),
                subscription.getStatus().name(),
                subscription.getAmount(),
                subscription.getBillingPeriod().getStartDate(),
                subscription.getBillingPeriod().getEndDate(),
                subscription.getScheduledPlanType() == null ? null : subscription.getScheduledPlanType().name(),
                subscription.getProratedChargeAmount(),
                subscription.isCancellationScheduled(),
                subscription.getCreatedAt()
        );
    }
}
