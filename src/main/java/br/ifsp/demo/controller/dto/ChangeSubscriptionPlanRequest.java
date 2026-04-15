package br.ifsp.demo.controller.dto;

import br.ifsp.demo.model.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeSubscriptionPlanRequest(
        @Schema(description = "New subscription plan", example = "PLUS")
        PlanType planType
) {
}
