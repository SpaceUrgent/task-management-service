package com.task.management.domain.project.application.service;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.project.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Slf4j
@AppComponent
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryPort projectUserRepositoryPort;

    public UserInfo getUser(UserId id) throws UseCaseException.EntityNotFoundException {
        parameterRequired(id, "User id");
        return projectUserRepositoryPort.find(id)
                .orElseThrow(() -> {
                    log.debug("User with id {} not found", id);
                    return new UseCaseException.EntityNotFoundException("User not found");
                });
    }

    public UserInfo getUser(Email email) throws UseCaseException.EntityNotFoundException {
        parameterRequired(email, "Email");
        return projectUserRepositoryPort.find(email)
                .orElseThrow(() -> {
                    log.debug("User with email {} not found", email);
                    return new UseCaseException.EntityNotFoundException("User with email '%s' not found".formatted(email));
                });
    }
}
