package com.task.management.persistence.jpa;

import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
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
    public JpaUserRepositoryAdapter userRepositoryAdapter(JpaUserRepository jpaUserRepository,
                                                          UserMapper userMapper) {
        return new JpaUserRepositoryAdapter(jpaUserRepository, userMapper);
    }

    @Bean
    public JpaProjectRepositoryAdapter projectRepositoryAdapter(JpaProjectRepository jpaProjectRepository,
                                                                ProjectMapper projectMapper) {
        return new JpaProjectRepositoryAdapter(jpaProjectRepository, projectMapper);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public ProjectMapper projectMapper(JpaUserRepository jpaUserRepository) {
        return new ProjectMapper(jpaUserRepository);
    }

}
