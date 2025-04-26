package com.task.management.domain.common.application.service;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.common.port.out.UserInfoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Slf4j
@AppComponent
public class UserInfoService {
    private final UserInfoRepositoryPort userInfoRepositoryPort;

    public UserInfoService(UserInfoRepositoryPort userInfoRepositoryPort) {
        this.userInfoRepositoryPort = userInfoRepositoryPort;
    }

    public UserInfo getUser(Email email) throws UseCaseException.EntityNotFoundException {
        parameterRequired(email, "Email");
        return userInfoRepositoryPort.find(email)
                .orElseThrow(() -> {
                    log.debug("User with email {} not found", email);
                    return new UseCaseException.EntityNotFoundException("User with email '%s' not found".formatted(email));
                });
    }
}
