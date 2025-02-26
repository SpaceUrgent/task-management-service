package com.task.management.persistence.jpa;

import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.dao.impl.ProjectEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.TaskEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.UserEntityDaoImpl;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@EntityScan(basePackages = "com.task.management.persistence.jpa.entity")
@Configuration
public class JpaTestConfiguration {

    @Bean
    public JpaUserRepositoryAdapter userRepositoryAdapter(UserEntityDao jpaUserRepository) {
        return new JpaUserRepositoryAdapter(jpaUserRepository);
    }

    @Bean
    public JpaProjectUserRepositoryAdapter jpaProjectUserRepositoryAdapter(UserEntityDao jpaUserRepository) {
        return new JpaProjectUserRepositoryAdapter(jpaUserRepository);
    }

    @Bean
    public JpaProjectRepositoryAdapter jpaProjectRepositoryAdapter(UserEntityDao userEntityDao,
                                                                   ProjectEntityDao projectEntityDao) {
        return new JpaProjectRepositoryAdapter(
                userEntityDao,
                projectEntityDao
        );
    }

    @Bean
    public JpaTaskRepositoryAdapter jpaTaskRepositoryAdapter(TaskEntityDao taskEntityDao,
                                                             ProjectEntityDao projectEntityDao,
                                                             UserEntityDao userEntityDao) {
        return new JpaTaskRepositoryAdapter(
                taskEntityDao,
                projectEntityDao,
                userEntityDao
        );
    }

    @Bean
    public UserEntityDao userDao(EntityManager entityManager) {
        return new UserEntityDaoImpl(entityManager);
    }

    @Bean
    public ProjectEntityDao projectEntityDao(EntityManager entityManager) {
        return new ProjectEntityDaoImpl(entityManager);
    }

    @Bean
    public TaskEntityDao taskEntityDao(EntityManager entityManager) {
        return new TaskEntityDaoImpl(entityManager);
    }
}
