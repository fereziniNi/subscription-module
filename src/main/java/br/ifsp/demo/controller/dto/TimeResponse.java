package br.ifsp.demo.controller.dto;

import java.time.LocalDate;

public record TimeResponse(
        LocalDate currentDate
) {
}
