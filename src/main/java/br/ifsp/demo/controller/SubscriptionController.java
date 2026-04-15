package br.ifsp.demo.controller;

import br.ifsp.demo.application.service.*;
import br.ifsp.demo.controller.dto.ChangeSubscriptionPlanRequest;
import br.ifsp.demo.controller.dto.CreateSubscriptionRequest;
import br.ifsp.demo.controller.dto.RenewSubscriptionRequest;
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
    private final CancelSubscriptionService cancelSubscriptionService;
    private final RenewSubscriptionService renewSubscriptionService;


    public SubscriptionController(
            CreateSubscriptionService createSubscriptionService,
            GetSubscriptionService getSubscriptionService,
            ListCustomerSubscriptionsService listCustomerSubscriptionsService,
            ChangeSubscriptionPlanService changeSubscriptionPlanService,
            CancelSubscriptionService cancelSubscriptionService,
            RenewSubscriptionService renewSubscriptionService
    ) {
        this.createSubscriptionService = createSubscriptionService;
        this.getSubscriptionService = getSubscriptionService;
        this.listCustomerSubscriptionsService = listCustomerSubscriptionsService;
        this.changeSubscriptionPlanService = changeSubscriptionPlanService;
        this.cancelSubscriptionService = cancelSubscriptionService;
        this.renewSubscriptionService = renewSubscriptionService;

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

    @Operation(
            summary = "Cancel subscription immediately",
            description = "Cancels an active or suspended subscription immediately."
    )
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelImmediately(@PathVariable UUID id) {
        Subscription subscription = cancelSubscriptionService.cancelImmediately(id);

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @Operation(
            summary = "Schedule subscription cancellation",
            description = "Schedules the subscription cancellation for the end of the current billing period."
    )
    @PostMapping("/{id}/cancel-at-period-end")
    public ResponseEntity<SubscriptionResponse> cancelAtPeriodEnd(@PathVariable UUID id) {
        Subscription subscription = cancelSubscriptionService.cancelAtPeriodEnd(id);

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @Operation(
            summary = "Reverse scheduled cancellation",
            description = "Removes a scheduled cancellation from the subscription."
    )
    @PostMapping("/{id}/reverse-cancellation")
    public ResponseEntity<SubscriptionResponse> reverseScheduledCancellation(@PathVariable UUID id) {
        Subscription subscription = cancelSubscriptionService.reverseScheduledCancellation(id);

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @Operation(
            summary = "Renew subscription",
            description = "Renews a subscription when the current billing period has expired."
    )
    @PostMapping("/{id}/renew")
    public ResponseEntity<SubscriptionResponse> renew(
            @PathVariable UUID id,
            @RequestBody RenewSubscriptionRequest request
    ) {
        Subscription subscription = renewSubscriptionService.renew(id, request.paymentApproved());

        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }





}
