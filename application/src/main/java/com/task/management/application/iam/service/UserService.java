package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.common.annotation.UseCase;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.port.in.GetUserProfileUseCase;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.application.iam.port.out.EncryptPasswordPort;
import com.task.management.application.iam.port.out.UserRepositoryPort;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.iam.model.User;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.actorIdRequired;

@AppComponent
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase,
                                    GetUserProfileUseCase {
    private final ValidationService validationService;
    private final EncryptPasswordPort encryptPasswordPort;
    private final UserRepositoryPort userRepositoryPort;

    @UseCase
    @Override
    public void register(final RegisterUserCommand command) throws EmailExistsException {
        validationService.validate(command);
        final var email = command.email();
        if (userRepositoryPort.emailExists(email)) {
            throw new EmailExistsException(email);
        }
        final var encryptedPassword = encryptPasswordPort.encrypt(command.password());
        var user = User.builder()
                .createdAt(Instant.now())
                .email(email)
                .firstName(command.firstName())
                .lastName(command.lastName())
                .encryptedPassword(encryptedPassword)
                .build();
        userRepositoryPort.save(user);
    }

    @UseCase
    @Override
    public UserInfo getUserProfile(UserId actorId) throws UseCaseException {
        actorIdRequired(actorId);
        return findOrThrow(actorId);
    }

    private UserInfo findOrThrow(UserId id) throws UseCaseException {
        return userRepositoryPort.find(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("User not found"));
    }
}
