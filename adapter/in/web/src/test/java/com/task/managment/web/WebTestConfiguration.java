package com.task.managment.web;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.out.UserRepository;
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
    public UserRepository userRepository() {
        return new UserRepository() {
            @Override
            public Optional<User> findById(UserId id) {
                return Optional.empty();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return Optional.empty();
            }

            @Override
            public User add(User user) {
                return null;
            }

            @Override
            public boolean emailExists(String email) {
                return false;
            }
        };
    }
}
