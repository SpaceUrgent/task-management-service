package com.task.management.persistence.jpa;

import com.task.management.application.port.out.UserRepository;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.task.management.persistence.jpa")
@EntityScan(basePackages = "com.task.management.persistence.jpa.entity")
@Configuration
public class JpaTestConfiguration {

    @Bean
    public UserRepository userRepositoryAdapter(JpaUserRepository jpaUserRepository,
                                                UserMapper userMapper) {
        return new JpaUserRepositoryAdapter(jpaUserRepository, userMapper);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }
}
