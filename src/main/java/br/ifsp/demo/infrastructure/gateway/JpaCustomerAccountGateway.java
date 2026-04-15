package br.ifsp.demo.infrastructure.gateway;

import br.ifsp.demo.application.gateway.CustomerAccountGateway;
import br.ifsp.demo.security.user.JpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JpaCustomerAccountGateway implements CustomerAccountGateway {

    private final JpaUserRepository userRepository;

    public JpaCustomerAccountGateway(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsById(UUID customerId) {
        return userRepository.existsById(customerId);
    }
}
