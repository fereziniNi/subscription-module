package br.ifsp.demo.security.config;

import br.ifsp.demo.config.MutableClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfiguration {

    @Bean
    public MutableClock systemClock() {
        return new MutableClock(Clock.systemDefaultZone());
    }
}
