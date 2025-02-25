package com.task.management.persistence.jpa;

import com.task.management.persistence.jpa.dao.UserDao;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.mapper.ProjectPreviewMapper;
import com.task.management.persistence.jpa.mapper.ProjectUserMapper;
import com.task.management.persistence.jpa.mapper.TaskDetailsMapper;
import com.task.management.persistence.jpa.mapper.TaskMapper;
import com.task.management.persistence.jpa.mapper.TaskPreviewMapper;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.mapper.UserProfileMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import com.task.management.persistence.jpa.repository.JpaTaskRepository;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import jakarta.persistence.EntityManager;
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

//    @Bean
//    public JpaUserRepositoryAdapter userRepositoryAdapter(JpaUserRepository jpaUserRepository,
//                                                          UserMapper userMapper,
//                                                          UserProfileMapper userProfileMapper) {
//        return new JpaUserRepositoryAdapter(jpaUserRepository, userMapper, userProfileMapper);
//    }

    @Bean
    public JpaUserRepositoryAdapter userRepositoryAdapter(UserDao jpaUserRepository,
                                                          UserMapper userMapper,
                                                          UserProfileMapper userProfileMapper) {
        return new JpaUserRepositoryAdapter(jpaUserRepository, userMapper, userProfileMapper);
    }

    @Bean
    public UserDao userDao(EntityManager entityManager) {
        return new UserDao(entityManager);
    }

    @Bean
    public JpaProjectUserRepositoryAdapter jpaProjectUserRepositoryAdapter(JpaUserRepository jpaUserRepository,
                                                                           ProjectUserMapper projectUserMapper) {
        return new JpaProjectUserRepositoryAdapter(jpaUserRepository, projectUserMapper);
    }

    @Bean
    public JpaProjectRepositoryAdapter jpaProjectRepositoryAdapter(JpaUserRepository jpaUserRepository,
                                                                   JpaProjectRepository jpaProjectRepository,
                                                                   ProjectMapper projectMapper,
                                                                   ProjectPreviewMapper projectPreviewMapper) {
        return new JpaProjectRepositoryAdapter(
                jpaUserRepository,
                jpaProjectRepository,
                projectMapper,
                projectPreviewMapper
        );
    }

    @Bean
    public JpaTaskRepositoryAdapter jpaTaskRepositoryAdapter(JpaTaskRepository jpaTaskRepository,
                                                             JpaProjectRepository jpaProjectRepository,
                                                             JpaUserRepository jpaUserRepository,
                                                             TaskMapper taskMapper,
                                                             TaskDetailsMapper taskDetailsMapper,
                                                             TaskPreviewMapper taskPreviewMapper) {
        return new JpaTaskRepositoryAdapter(
                jpaTaskRepository,
                jpaProjectRepository,
                jpaUserRepository,
                taskMapper,
                taskDetailsMapper,
                taskPreviewMapper
        );
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public UserProfileMapper userProfileMapper() {
        return new UserProfileMapper();
    }

    @Bean
    public ProjectUserMapper projectUserMapper() {
        return new ProjectUserMapper();
    }

    @Bean
    public ProjectMapper projectMapper(ProjectUserMapper projectUserMapper) {
        return new ProjectMapper(projectUserMapper);
    }

    @Bean
    public ProjectPreviewMapper projectPreviewMapper(ProjectUserMapper projectUserMapper) {
        return new ProjectPreviewMapper(projectUserMapper);
    }

    @Bean
    public TaskMapper taskMapper(ProjectUserMapper projectUserMapper) {
        return new TaskMapper(projectUserMapper);
    }

    @Bean
    public TaskDetailsMapper taskDetailsMapper(ProjectUserMapper projectUserMapper) {
        return new TaskDetailsMapper(projectUserMapper);
    }

    @Bean
    public TaskPreviewMapper taskPreviewMapper(ProjectUserMapper projectUserMapper) {
        return new TaskPreviewMapper(projectUserMapper);
    }
}
