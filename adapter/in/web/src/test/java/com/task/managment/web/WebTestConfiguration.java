package com.task.managment.web;

import com.task.management.application.iam.model.UserCredentials;
import com.task.management.application.iam.port.out.FindUserCredentialsPort;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ComponentScan(basePackages = "com.task.managment.web.mapper")
@EnableAutoConfiguration
@Configuration
public class WebTestConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FindUserCredentialsPort findUserPort() {
        return new FindUserCredentialsPort() {
            @Override
            public Optional<UserCredentials> findByEmail(String email) {
                return Optional.empty();
            }
        };
    }
}
