package com.task.management.spring.configuration;

import com.task.management.application.common.annotation.AppComponent;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan(basePackages = {
        "com.task.managment.web",
        "com.task.management.domain",
        "com.task.management.password",
        "com.task.management.persistence"
}, includeFilters = @ComponentScan.Filter(AppComponent.class))
@EntityScan(basePackages = "com.task.management.persistence.jpa.entity")
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
