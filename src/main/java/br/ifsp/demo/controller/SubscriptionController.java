package br.ifsp.demo.controller;

import br.ifsp.demo.application.service.ChangeSubscriptionPlanService;
import br.ifsp.demo.application.service.CreateSubscriptionService;
import br.ifsp.demo.application.service.GetSubscriptionService;
import br.ifsp.demo.application.service.ListCustomerSubscriptionsService;
import br.ifsp.demo.controller.dto.ChangeSubscriptionPlanRequest;
import br.ifsp.demo.controller.dto.CreateSubscriptionRequest;
import br.ifsp.demo.controller.dto.SubscriptionResponse;
import br.ifsp.demo.model.Subscription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription API")
public class SubscriptionController {

    private final CreateSubscriptionService createSubscriptionService;
    private final GetSubscriptionService getSubscriptionService;
    private final ListCustomerSubscriptionsService listCustomerSubscriptionsService;
    private final ChangeSubscriptionPlanService changeSubscriptionPlanService;

    public SubscriptionController(
            CreateSubscriptionService createSubscriptionService,
            GetSubscriptionService getSubscriptionService,
            ListCustomerSubscriptionsService listCustomerSubscriptionsService,
            ChangeSubscriptionPlanService changeSubscriptionPlanService
    ) {
        this.createSubscriptionService = createSubscriptionService;
        this.getSubscriptionService = getSubscriptionService;
        this.listCustomerSubscriptionsService = listCustomerSubscriptionsService;
        this.changeSubscriptionPlanService = changeSubscriptionPlanService;
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

    @Operation(
            summary = "Get subscription by id",
            description = "Returns a subscription by its identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable UUID id) {
        Subscription subscription = getSubscriptionService.getById(id);

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @Operation(
            summary = "List customer subscriptions",
            description = "Returns all subscriptions that belong to a customer."
    )
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<SubscriptionResponse>> listByCustomerId(@PathVariable UUID customerId) {
        List<SubscriptionResponse> subscriptions = listCustomerSubscriptionsService.findByCustomerId(customerId)
                .stream()
                .map(SubscriptionResponse::from)
                .toList();

        return ResponseEntity.ok(subscriptions);
    }

    @Operation(
            summary = "Change subscription plan",
            description = "Changes a subscription plan immediately for upgrades or schedules the change for downgrades."
    )
    @PatchMapping("/{id}/plan")
    public ResponseEntity<SubscriptionResponse> changePlan(
            @PathVariable UUID id,
            @RequestBody ChangeSubscriptionPlanRequest request
    ) {
        Subscription subscription = changeSubscriptionPlanService.changePlan(id, request.planType());

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }



}
