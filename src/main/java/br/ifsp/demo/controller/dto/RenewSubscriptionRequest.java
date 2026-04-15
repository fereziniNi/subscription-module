package br.ifsp.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RenewSubscriptionRequest(
        @Schema(description = "Indicates whether the renewal payment was approved", example = "true")
        boolean paymentApproved
) {
}
