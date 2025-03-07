package com.task.management.spring.configuration;

import com.task.management.domain.common.ValidationService;
import com.task.management.domain.iam.port.out.AddUserPort;
import com.task.management.domain.iam.port.out.EmailExistsPort;
import com.task.management.domain.iam.port.out.EncryptPasswordPort;
import com.task.management.domain.iam.port.out.FindUserProfileByIdPort;
import com.task.management.domain.iam.service.UserService;
import com.task.management.domain.project.port.out.AddProjectMemberPort;
import com.task.management.domain.project.port.out.AddProjectPort;
import com.task.management.domain.project.port.out.AddTaskPort;
import com.task.management.domain.project.port.out.FindProjectByIdPort;
import com.task.management.domain.project.port.out.FindProjectMemberPort;
import com.task.management.domain.project.port.out.FindProjectMembersPort;
import com.task.management.domain.project.port.out.FindProjectTasksPort;
import com.task.management.domain.project.port.out.FindProjectUserByEmailPort;
import com.task.management.domain.project.port.out.FindProjectUserByIdPort;
import com.task.management.domain.project.port.out.FindProjectsByMemberPort;
import com.task.management.domain.project.port.out.FindTaskByIdPort;
import com.task.management.domain.project.port.out.FindTaskDetailsByIdPort;
import com.task.management.domain.project.port.out.UpdateProjectPort;
import com.task.management.domain.project.port.out.UpdateTaskPort;
import com.task.management.domain.project.service.ProjectService;
import com.task.management.domain.project.service.ProjectUserService;
import com.task.management.domain.project.service.TaskService;
import com.task.management.password.encryption.BCryptPasswordEncryptorAdapter;
import com.task.management.persistence.jpa.JpaProjectRepositoryAdapter;
import com.task.management.persistence.jpa.JpaProjectUserRepositoryAdapter;
import com.task.management.persistence.jpa.JpaTaskRepositoryAdapter;
import com.task.management.persistence.jpa.JpaUserRepositoryAdapter;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.dao.impl.ProjectEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.TaskEntityDaoImpl;
import com.task.management.persistence.jpa.dao.impl.UserEntityDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan(basePackages = {
        "com.task.managment.web"
})
public class BeanConfiguration {

    @Bean
    public UserService userService(ValidationService validationService,
                                   EncryptPasswordPort encryptPasswordPort,
                                   EmailExistsPort emailExistsPort,
                                   AddUserPort addUserPort,
                                   FindUserProfileByIdPort findUserProfileByIdPort) {
        return new UserService(
                validationService,
                encryptPasswordPort,
                emailExistsPort,
                addUserPort,
                findUserProfileByIdPort
        );
    }

    @Bean
    public ProjectService projectService(ValidationService validationService,
                                         ProjectUserService projectUserService,
                                         AddProjectPort addProjectPort,
                                         UpdateProjectPort updateProjectPort,
                                         AddProjectMemberPort addProjectMemberPort,
                                         FindProjectByIdPort findProjectByIdPort,
                                         FindProjectsByMemberPort findProjectsByMemberPort,
                                         FindProjectMembersPort findProjectMembersPort) {
        return new ProjectService(
                validationService,
                projectUserService,
                addProjectPort,
                updateProjectPort,
                addProjectMemberPort,
                findProjectByIdPort,
                findProjectsByMemberPort,
                findProjectMembersPort
        );
    }

    @Bean
    public ProjectUserService projectUserService(FindProjectUserByIdPort findProjectUserByIdPort,
                                                 FindProjectUserByEmailPort findProjectUserByEmailPort,
                                                 FindProjectMemberPort findProjectMemberPort) {
        return new ProjectUserService(
                findProjectUserByIdPort,
                findProjectUserByEmailPort,
                findProjectMemberPort
        );
    }

    @Bean
    public TaskService taskService(ValidationService validationService,
                                   ProjectUserService projectUserService,
                                   AddTaskPort addTaskPort,
                                   UpdateTaskPort updateTaskPort,
                                   FindTaskByIdPort findTaskByIdPort,
                                   FindTaskDetailsByIdPort findTaskDetailsByIdPort,
                                   FindProjectTasksPort findProjectTasksPort) {
        return new TaskService(
                validationService,
                projectUserService,
                addTaskPort,
                updateTaskPort,
                findTaskByIdPort,
                findTaskDetailsByIdPort,
                findProjectTasksPort
        );
    }

    @Bean
    public ValidationService validationService() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return new ValidationService(validatorFactory.getValidator());
        }
    }

    @Bean
    public EncryptPasswordPort encryptPasswordPort(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordEncryptorAdapter(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JpaUserRepositoryAdapter jpaUserRepositoryAdapter(UserEntityDao userEntityDao) {
        return new JpaUserRepositoryAdapter(userEntityDao);
    }

    @Bean
    public JpaProjectRepositoryAdapter jpaProjectRepositoryAdapter(UserEntityDao userEntityDao,
                                                                   ProjectEntityDao projectEntityDao) {
        return new JpaProjectRepositoryAdapter(userEntityDao, projectEntityDao);
    }

    @Bean
    public JpaProjectUserRepositoryAdapter jpaProjectUserRepositoryAdapter(UserEntityDao userEntityDao) {
        return new JpaProjectUserRepositoryAdapter(userEntityDao);
    }

    @Bean
    public JpaTaskRepositoryAdapter jpaTaskRepositoryAdapter(TaskEntityDao taskEntityDao,
                                                             ProjectEntityDao projectEntityDao,
                                                             UserEntityDao userEntityDao) {
        return new JpaTaskRepositoryAdapter(taskEntityDao, projectEntityDao, userEntityDao);
    }

    @Bean
    public UserEntityDao userEntityDao(EntityManager entityManager) {
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
