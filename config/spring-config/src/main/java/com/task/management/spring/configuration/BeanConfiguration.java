package com.task.management.spring.configuration;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.event.SimpleDomainEventPublisher;
import com.task.management.application.shared.event.DomainEventHandler;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@ComponentScan(basePackages = {
        "com.task.managment.web",
        "com.task.management.application",
        "com.task.management.password",
        "com.task.management.persistence"
}, includeFilters = @ComponentScan.Filter(AppComponent.class))
@EntityScan(basePackages = "com.task.management.persistence.jpa.entity")
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DomainEventPublisherPort domainEventPublisherPort() {
        return new SimpleDomainEventPublisher();
    }

    @Bean
    public InitializingBean wireEventHandlers(SimpleDomainEventPublisher domainEventPublisher, List<DomainEventHandler<?>> eventHandlers) {
        return () -> {
            domainEventPublisher.register(eventHandlers);
        };
    }
}
