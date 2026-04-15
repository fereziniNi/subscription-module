package br.ifsp.demo.controller.dto;

import br.ifsp.demo.model.BillingCycle;
import br.ifsp.demo.model.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @Schema(description = "Customer identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID customerId,

        @Schema(description = "Subscription plan", example = "BASIC")
        PlanType planType,

        @Schema(description = "Billing cycle", example = "MONTHLY")
        BillingCycle billingCycle
) {
}
