package br.ifsp.demo.application.gateway;

import java.util.UUID;

public interface CustomerAccountGateway {
    boolean existsById(UUID customerId);
}