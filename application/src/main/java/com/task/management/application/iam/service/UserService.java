package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.common.annotation.UseCase;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.command.UpdatePasswordCommand;
import com.task.management.application.iam.port.in.UserProfileUseCase;
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
public class UserService implements RegisterUserUseCase, UserProfileUseCase {
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
        return userRepositoryPort.findUserInfo(actorId)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("User not found"));
    }

    @UseCase
    @Override
    public void updateName(UserId actorId, UpdateNameCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        validationService.validate(command);
        final var user = getUser(actorId);
        user.updateName(command.firstName(), command.lastName());
        userRepositoryPort.save(user);
    }

    @UseCase
    @Override
    public void updatePassword(UserId actorId, UpdatePasswordCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        validationService.validate(command);
        final var user = getUser(actorId);
        final var oldPassword = command.oldPassword();
        final var encryptedOldPassword = encryptPasswordPort.encrypt(oldPassword);
        if (!encryptedOldPassword.equals(user.getEncryptedPassword())) {
            throw new UseCaseException.IllegalAccessException("Operation not allowed");
        }
        user.updatePassword(encryptPasswordPort.encrypt(command.newPassword()));
        userRepositoryPort.save(user);
    }

    private User getUser(UserId actorId) throws UseCaseException.EntityNotFoundException {
        return userRepositoryPort.find(actorId)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("User not found"));
    }
}
