package com.task.management.application.shared.service;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.UseCaseException;
import com.task.management.application.shared.port.out.UserInfoRepositoryPort;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.UserInfo;
import lombok.extern.slf4j.Slf4j;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
                    log.debug("User with email {} not found", email.value());
                    return new UseCaseException.EntityNotFoundException("User with email '%s' not found".formatted(email.value()));
                });
    }
}
