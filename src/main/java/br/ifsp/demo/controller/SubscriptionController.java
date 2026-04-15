package br.ifsp.demo.controller;

import br.ifsp.demo.application.service.CreateSubscriptionService;
import br.ifsp.demo.controller.dto.CreateSubscriptionRequest;
import br.ifsp.demo.controller.dto.SubscriptionResponse;
import br.ifsp.demo.model.Subscription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription API")
public class SubscriptionController {

    private final CreateSubscriptionService createSubscriptionService;

    public SubscriptionController(CreateSubscriptionService createSubscriptionService) {
        this.createSubscriptionService = createSubscriptionService;
    }

    @Operation(
            summary = "Create subscription",
            description = "Creates a subscription for an existing customer using the selected plan and billing cycle."
    )
    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody CreateSubscriptionRequest request) {
        Subscription subscription = createSubscriptionService.create(
                request.customerId(),
                request.planType(),
                request.billingCycle()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubscriptionResponse.from(subscription));
    }
}
