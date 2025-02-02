package com.task.managment.web;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.CreateProjectDto;
import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.UpdateProjectDto;
import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.GetUserByEmailUseCase;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.port.out.FindUserPort;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@EnableAutoConfiguration
@Configuration
public class WebTestConfiguration {

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
    public FindUserPort findUserPort() {
        return new FindUserPort() {
            @Override
            public Optional<User> findById(UserId id) {
                return Optional.empty();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public CreateProjectUseCase createProjectUseCase() {
        return (userId, createProjectDto) -> null;
    }

    @Bean
    public GetAvailableProjectsUseCase getAvailableProjectsUseCase() {
        return (userId, page) -> List.of();
    }

    @Bean
    public GetProjectDetailsUseCase getProjectDetailsUseCase() {
        return (currentUser, projectId) -> null;
    }

    @Bean
    public UpdateProjectUseCase updateProjectUseCase() {
        return (currentUser, projectId, updateProjectDto) -> null;
    }

    @Bean
    public AddProjectMemberUseCase addProjectMemberUseCase() {
        return (currentUserId, projectId, memberId) -> {};
    }

    @Bean
    public GetUserByEmailUseCase getUserByEmailUseCase() {
        return email -> null;
    }
}
