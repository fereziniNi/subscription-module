package br.ifsp.demo.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("UnitTest")
@Tag("Structural")
class BillingPeriodTest {

    @Test
    void shouldCreateBillingPeriodWhenDatesAreValid() {
        LocalDate startDate = LocalDate.of(2026, 4, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 1);

        BillingPeriod billingPeriod = new BillingPeriod(startDate, endDate);

        assertThat(billingPeriod.getStartDate()).isEqualTo(startDate);
        assertThat(billingPeriod.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void shouldThrowErrorWhenStartDateIsNull() {
        LocalDate endDate = LocalDate.of(2026, 5, 1);

        assertThatThrownBy(() -> new BillingPeriod(null, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Billing period dates must not be null");
    }

    @Test
    void shouldThrowErrorWhenEndDateIsNull() {
        LocalDate startDate = LocalDate.of(2026, 4, 1);

        assertThatThrownBy(() -> new BillingPeriod(startDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Billing period dates must not be null");
    }

    @Test
    void shouldThrowErrorWhenEndDateIsEqualToStartDate() {
        LocalDate date = LocalDate.of(2026, 4, 1);

        assertThatThrownBy(() -> new BillingPeriod(date, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date must be after start date");
    }

    @Test
    void shouldThrowErrorWhenEndDateIsBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 4, 1);

        assertThatThrownBy(() -> new BillingPeriod(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date must be after start date");
    }
}