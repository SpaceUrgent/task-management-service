package com.task.management.domain.project.service;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.out.ProjectUserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Slf4j
@AppComponent
@RequiredArgsConstructor
public class ProjectUserService  {
    private final ProjectUserRepositoryPort projectUserRepositoryPort;

    public ProjectUser getProjectUser(ProjectUserId id) throws UseCaseException.EntityNotFoundException {
        parameterRequired(id, "User id");
        return projectUserRepositoryPort.find(id)
                .orElseThrow(() -> {
                    log.debug("User with id {} not found", id);
                    return new UseCaseException.EntityNotFoundException("User not found");
                });
    }

    public ProjectUser getProjectUser(Email email) throws UseCaseException.EntityNotFoundException {
        parameterRequired(email, "Email");
        return projectUserRepositoryPort.find(email)
                .orElseThrow(() -> {
                    log.debug("User with email {} not found", email);
                    return new UseCaseException.EntityNotFoundException("User with email '%s' not found".formatted(email));
                });
    }
}
