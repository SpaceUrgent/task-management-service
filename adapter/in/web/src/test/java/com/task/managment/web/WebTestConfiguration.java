package com.task.managment.web;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.out.FindUserPort;
import com.task.managment.web.mapper.WebProjectMapper;
import com.task.managment.web.mapper.WebUserMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@EnableAutoConfiguration
@Configuration
public class WebTestConfiguration {

    @Bean
    public WebUserMapper webUserMapper() {
        return new WebUserMapper();
    }

    @Bean
    public WebProjectMapper webProjectMapper(WebUserMapper webUserMapper) {
        return new WebProjectMapper(webUserMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase() {
        return registerUserDto -> null;
    }

    @Bean
    public GetUserUseCase getUserUseCase() {
        return id -> null;
    }

    @Bean
    public FindUserPort findUserPort() {
        return new FindUserPort() {
            @Override
            public Optional<User> findById(UserId id) {
                return Optional.empty();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return Optional.empty();
            }
        };
    }
}
