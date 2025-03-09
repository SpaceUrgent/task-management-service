package com.task.management.spring.configuration;

import com.task.management.domain.common.annotation.AppComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan(basePackages = {
        "com.task.managment.web",
        "com.task.management.domain",
        "com.task.management.password",
        "com.task.management.persistence"
}, includeFilters = @ComponentScan.Filter(AppComponent.class))
public class BeanConfiguration {

//    @Bean
//    public UserService userService(ValidationService validationService,
//                                   EncryptPasswordPort encryptPasswordPort,
//                                   EmailExistsPort emailExistsPort,
//                                   AddUserPort addUserPort,
//                                   FindUserProfileByIdPort findUserProfileByIdPort) {
//        return new UserService(
//                validationService,
//                encryptPasswordPort,
//                emailExistsPort,
//                addUserPort,
//                findUserProfileByIdPort
//        );
//    }
//
//    @Bean
//    public ProjectService projectService(ValidationService validationService,
//                                         ProjectUserService projectUserService,
//                                         AddProjectPort addProjectPort,
//                                         UpdateProjectPort updateProjectPort,
//                                         AddProjectMemberPort addProjectMemberPort,
//                                         FindProjectByIdPort findProjectByIdPort,
//                                         FindProjectsByMemberPort findProjectsByMemberPort,
//                                         FindProjectMembersPort findProjectMembersPort) {
//        return new ProjectService(
//                validationService,
//                projectUserService,
//                addProjectPort,
//                updateProjectPort,
//                addProjectMemberPort,
//                findProjectByIdPort,
//                findProjectsByMemberPort,
//                findProjectMembersPort
//        );
//    }
//
//    @Bean
//    public ProjectUserService projectUserService(FindProjectUserByIdPort findProjectUserByIdPort,
//                                                 FindProjectUserByEmailPort findProjectUserByEmailPort,
//                                                 FindProjectMemberPort findProjectMemberPort) {
//        return new ProjectUserService(
//                findProjectUserByIdPort,
//                findProjectUserByEmailPort,
//                findProjectMemberPort
//        );
//    }
//
//    @Bean
//    public TaskService taskService(ValidationService validationService,
//                                   ProjectUserService projectUserService,
//                                   AddTaskPort addTaskPort,
//                                   UpdateTaskPort updateTaskPort,
//                                   FindTaskByIdPort findTaskByIdPort,
//                                   FindTaskDetailsByIdPort findTaskDetailsByIdPort,
//                                   FindProjectTasksPort findProjectTasksPort) {
//        return new TaskService(
//                validationService,
//                projectUserService,
//                addTaskPort,
//                updateTaskPort,
//                findTaskByIdPort,
//                findTaskDetailsByIdPort,
//                findProjectTasksPort
//        );
//    }
//
//    @Bean
//    public ValidationService validationService() {
//        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
//            return new ValidationService(validatorFactory.getValidator());
//        }
//    }
//
//    @Bean
//    public EncryptPasswordPort encryptPasswordPort(PasswordEncoder passwordEncoder) {
//        return new BCryptPasswordEncryptorAdapter(passwordEncoder);
//    }
//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//
//    @Bean
//    public JpaUserRepositoryAdapter jpaUserRepositoryAdapter(UserEntityDao userEntityDao) {
//        return new JpaUserRepositoryAdapter(userEntityDao);
//    }
//
//    @Bean
//    public JpaProjectRepositoryAdapter jpaProjectRepositoryAdapter(UserEntityDao userEntityDao,
//                                                                   ProjectEntityDao projectEntityDao) {
//        return new JpaProjectRepositoryAdapter(userEntityDao, projectEntityDao);
//    }
//
//    @Bean
//    public JpaProjectUserRepositoryAdapter jpaProjectUserRepositoryAdapter(UserEntityDao userEntityDao) {
//        return new JpaProjectUserRepositoryAdapter(userEntityDao);
//    }
//
//    @Bean
//    public JpaTaskRepositoryAdapter jpaTaskRepositoryAdapter(TaskEntityDao taskEntityDao,
//                                                             ProjectEntityDao projectEntityDao,
//                                                             UserEntityDao userEntityDao) {
//        return new JpaTaskRepositoryAdapter(taskEntityDao, projectEntityDao, userEntityDao);
//    }
//
//    @Bean
//    public UserEntityDao userEntityDao(EntityManager entityManager) {
//        return new UserEntityDaoImpl(entityManager);
//    }
//
//    @Bean
//    public ProjectEntityDao projectEntityDao(EntityManager entityManager) {
//        return new ProjectEntityDaoImpl(entityManager);
//    }
//
//    @Bean
//    public TaskEntityDao taskEntityDao(EntityManager entityManager) {
//        return new TaskEntityDaoImpl(entityManager);
//    }
}
