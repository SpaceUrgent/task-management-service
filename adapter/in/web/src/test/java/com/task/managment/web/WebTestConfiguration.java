package com.task.managment.web;

import com.task.management.domain.common.interfaces.UserCredentialsPort;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

//@ComponentScan(basePackages = "com.task.managment.web")
@EnableAutoConfiguration
@Configuration
public class WebTestConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserCredentialsPort findUserPort() {
        return email -> Optional.empty();
    }
}
