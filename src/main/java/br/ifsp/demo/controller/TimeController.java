package br.ifsp.demo.controller;

import br.ifsp.demo.application.service.ProcessExpiredSubscriptionsService;
import br.ifsp.demo.config.MutableClock;
import br.ifsp.demo.controller.dto.TimeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/time")
@Tag(name = "Time API")
public class TimeController {

    private final MutableClock mutableClock;
    private final ProcessExpiredSubscriptionsService processExpiredSubscriptionsService;

    public TimeController(
            MutableClock mutableClock,
            ProcessExpiredSubscriptionsService processExpiredSubscriptionsService
    ) {
        this.mutableClock = mutableClock;
        this.processExpiredSubscriptionsService = processExpiredSubscriptionsService;
    }

    @Operation(
            summary = "Get simulated current date",
            description = "Returns the current date used by the application clock."
    )
    @GetMapping
    public ResponseEntity<TimeResponse> getCurrentDate() {
        return ResponseEntity.ok(response());
    }

    @Operation(
            summary = "Advance simulated time",
            description = "Advances the application clock by the requested number of months."
    )
    @PostMapping("/advance/{months}")
    public ResponseEntity<TimeResponse> advanceMonths(@PathVariable int months) {
        mutableClock.advanceMonths(months);
        processExpiredSubscriptionsService.processExpiredSubscriptions();

        return ResponseEntity.ok(response());
    }

    @Operation(
            summary = "Reset simulated time",
            description = "Resets the application clock to the current system date."
    )
    @PostMapping("/reset")
    public ResponseEntity<TimeResponse> reset() {
        mutableClock.reset();

        return ResponseEntity.ok(response());
    }

    private TimeResponse response() {
        return new TimeResponse(mutableClock.currentDate());
    }
}
