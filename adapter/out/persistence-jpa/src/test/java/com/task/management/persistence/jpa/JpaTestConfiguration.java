package com.task.management.persistence.jpa;

import com.task.management.persistence.jpa.repository.*;
import com.task.management.persistence.jpa.dao.*;
import com.task.management.persistence.jpa.dao.impl.*;
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
    public MemberEntityDao memberEntityDao(EntityManager entityManager) {
        return new MemberEntityDaoImpl(entityManager);
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
                                                             UserEntityDao userEntityDao,
                                                             TaskChangeLogEntityDao taskChangeLogEntityDao) {
        return new JpaTaskRepositoryAdapter(
                taskEntityDao,
                taskChangeLogEntityDao,
                taskNumberSequenceDao,
                projectEntityDao,
                userEntityDao
        );
    }

    @Bean
    public JpaTaskCommentRepositoryAdapter jpaTaskCommentRepositoryAdapter(TaskCommentEntityDao taskCommentEntityDao,
                                                                           TaskEntityDao taskEntityDao,
                                                                           UserEntityDao userEntityDao) {
        return new JpaTaskCommentRepositoryAdapter(
                taskCommentEntityDao,
                taskEntityDao,
                userEntityDao
        );
    }

    @Bean
    public JpaUserInfoRepositoryAdapter jpaUserInfoRepositoryAdapter(UserEntityDao userEntityDao) {
        return new JpaUserInfoRepositoryAdapter(userEntityDao);
    }

    @Bean
    public JpaTasksDashboardRepositoryAdapter jpaTasksDashboardRepositoryAdapter(TaskEntityDao taskEntityDao) {
        return new JpaTasksDashboardRepositoryAdapter(taskEntityDao);
    }

    @Bean
    public JpaMemberRepositoryAdapter jpaMemberRepositoryAdapter(MemberEntityDao memberEntityDao,
                                                                 UserEntityDao userEntityDao,
                                                                 ProjectEntityDao projectEntityDao) {
        return new JpaMemberRepositoryAdapter(memberEntityDao, userEntityDao, projectEntityDao);
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
    public TaskChangeLogEntityDao taskChangeLogEntityDao(EntityManager entityManager) {
        return new TaskChangeLogEntityDaoImpl(entityManager);
    }

    @Bean
    public TaskNumberSequenceDao taskNumberSequenceDao(EntityManager entityManager) {
        return new TaskNumberSequenceDaoImpl(entityManager);
    }

    @Bean
    public TaskCommentEntityDao taskCommentEntityDao(EntityManager entityManager) {
        return new TaskCommentEntityDaoImpl(entityManager);
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }
}
