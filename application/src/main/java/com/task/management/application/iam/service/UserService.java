package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.common.annotation.UseCase;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.iam.CurrentPasswordMismatchException;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.command.UpdatePasswordCommand;
import com.task.management.application.iam.port.in.UserProfileUseCase;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.application.iam.port.out.EncryptPasswordPort;
import com.task.management.application.iam.port.out.UserRepositoryPort;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.iam.model.User;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.actorIdRequired;

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
        if (!encryptPasswordPort.matches(command.currentPassword(), user.getEncryptedPassword())) {
            throw new CurrentPasswordMismatchException("Current password does not match");
        }
        user.updatePassword(encryptPasswordPort.encrypt(command.newPassword()));
        userRepositoryPort.save(user);
    }

    private User getUser(UserId actorId) throws UseCaseException.EntityNotFoundException {
        return userRepositoryPort.find(actorId)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("User not found"));
    }
}
