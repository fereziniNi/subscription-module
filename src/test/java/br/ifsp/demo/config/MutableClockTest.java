package br.ifsp.demo.config;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
class MutableClockTest {

    @Test
    void shouldAdvanceCurrentDateByMonths() {
        MutableClock clock = new MutableClock(fixedClock());

        LocalDate advancedDate = clock.advanceMonths(1);

        assertThat(advancedDate).isEqualTo(LocalDate.of(2026, 6, 14));
        assertThat(clock.currentDate()).isEqualTo(LocalDate.of(2026, 6, 14));
    }

    @Test
    void shouldResetToSourceClockDate() {
        MutableClock clock = new MutableClock(fixedClock());

        clock.advanceMonths(12);
        LocalDate resetDate = clock.reset();

        assertThat(resetDate).isEqualTo(LocalDate.of(2026, 5, 14));
        assertThat(clock.currentDate()).isEqualTo(LocalDate.of(2026, 5, 14));
    }

    @Test
    void shouldRejectNonPositiveMonthAdvances() {
        MutableClock clock = new MutableClock(fixedClock());

        assertThatThrownBy(() -> clock.advanceMonths(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Months must be positive");
    }

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-05-14T00:00:00Z"),
                ZoneId.of("UTC")
        );
    }
}
