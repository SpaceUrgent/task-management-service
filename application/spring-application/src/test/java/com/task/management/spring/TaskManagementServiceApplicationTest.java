package com.task.management.spring;

import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.iam.port.in.GetUserProfileUseCase;
import com.task.management.domain.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.iam.port.out.EncryptPasswordPort;
import com.task.management.domain.project.port.in.AddProjectMemberUseCase;
import com.task.management.domain.project.port.in.AssignTaskUseCase;
import com.task.management.domain.project.port.in.CreateProjectUseCase;
import com.task.management.domain.project.port.in.CreateTaskUseCase;
import com.task.management.domain.project.port.in.FindTasksUseCase;
import com.task.management.domain.project.port.in.GetAvailableProjectsUseCase;
import com.task.management.domain.project.port.in.GetProjectDetailsUseCase;
import com.task.management.domain.project.port.in.GetProjectMembersUseCase;
import com.task.management.domain.project.port.in.GetTaskDetailsUseCase;
import com.task.management.domain.project.port.in.UpdateProjectUseCase;
import com.task.management.domain.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.domain.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import com.task.management.domain.project.port.out.UserRepositoryPort;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TaskManagementServiceApplication.class, TestConfigurations.class})
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=update"})
class TaskManagementServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TestUseCaseService testUseCaseService;

    @Test
    void beanTest() {
        assertNotNull(applicationContext.getBean(ValidationService.class));
        assertNotNull(applicationContext.getBean(GetUserProfileUseCase.class));
        assertNotNull(applicationContext.getBean(RegisterUserUseCase.class));
        assertNotNull(applicationContext.getBean(EncryptPasswordPort.class));
        assertNotNull(applicationContext.getBean(com.task.management.domain.iam.port.out.UserRepositoryPort.class));

        assertNotNull(applicationContext.getBean(AddProjectMemberUseCase.class));
        assertNotNull(applicationContext.getBean(AssignTaskUseCase.class));
        assertNotNull(applicationContext.getBean(CreateProjectUseCase.class));
        assertNotNull(applicationContext.getBean(CreateTaskUseCase.class));
        assertNotNull(applicationContext.getBean(FindTasksUseCase.class));
        assertNotNull(applicationContext.getBean(GetAvailableProjectsUseCase.class));
        assertNotNull(applicationContext.getBean(GetProjectDetailsUseCase.class));
        assertNotNull(applicationContext.getBean(GetProjectMembersUseCase.class));
        assertNotNull(applicationContext.getBean(GetTaskDetailsUseCase.class));
        assertNotNull(applicationContext.getBean(UpdateProjectUseCase.class));
        assertNotNull(applicationContext.getBean(UpdateTaskStatusUseCase.class));
        assertNotNull(applicationContext.getBean(UpdateTaskUseCase.class));
        assertNotNull(applicationContext.getBean(ProjectRepositoryPort.class));
        assertNotNull(applicationContext.getBean(UserRepositoryPort.class));
        assertNotNull(applicationContext.getBean(TaskRepositoryPort.class));
    }

    @Test
    void transactionalUseCaseAspectTest() {
        assertTrue(testUseCaseService.isActiveTransaction());
    }
}