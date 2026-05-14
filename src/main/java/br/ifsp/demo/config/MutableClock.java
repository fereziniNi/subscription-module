package br.ifsp.demo.config;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MutableClock extends Clock {

    private final Clock sourceClock;
    private final ZoneId zone;
    private final AtomicReference<Instant> currentInstant;

    public MutableClock(Clock sourceClock) {
        this(sourceClock, sourceClock.getZone(), new AtomicReference<>(sourceClock.instant()));
    }

    private MutableClock(Clock sourceClock, ZoneId zone, AtomicReference<Instant> currentInstant) {
        this.sourceClock = Objects.requireNonNull(sourceClock);
        this.zone = Objects.requireNonNull(zone);
        this.currentInstant = Objects.requireNonNull(currentInstant);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        if (this.zone.equals(zone)) {
            return this;
        }

        return new MutableClock(sourceClock.withZone(zone), zone, currentInstant);
    }

    @Override
    public Instant instant() {
        return currentInstant.get();
    }

    public LocalDate currentDate() {
        return LocalDate.now(this);
    }

    public LocalDate advanceMonths(long months) {
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }

        currentInstant.updateAndGet(current ->
                LocalDateTime.ofInstant(current, zone)
                        .plusMonths(months)
                        .atZone(zone)
                        .toInstant()
        );

        return currentDate();
    }

    public LocalDate reset() {
        currentInstant.set(sourceClock.instant());

        return currentDate();
    }
}
