package com.task.management.persistence.jpa;

import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.TaskNumberSequenceDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.dao.impl.ProjectEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.TaskEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.TaskNumberSequenceDaoImpl;
import com.task.management.persistence.jpa.dao.impl.UserEntityDaoImpl;
import com.task.management.persistence.jpa.iam.JpaUserRepositoryAdapter;
import com.task.management.persistence.jpa.project.JpaProjectRepositoryAdapter;
import com.task.management.persistence.jpa.project.JpaProjectUserRepositoryAdapter;
import com.task.management.persistence.jpa.project.JpaTaskRepositoryAdapter;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

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
                                                             TaskNumberSequenceDao taskNumberSequenceDao,
                                                             ProjectEntityDao projectEntityDao,
                                                             UserEntityDao userEntityDao) {
        return new JpaTaskRepositoryAdapter(
                taskEntityDao,
                taskNumberSequenceDao,
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

    @Bean
    public TaskNumberSequenceDao taskNumberSequenceDao(EntityManager entityManager) {
        return new TaskNumberSequenceDaoImpl(entityManager);
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }
}
