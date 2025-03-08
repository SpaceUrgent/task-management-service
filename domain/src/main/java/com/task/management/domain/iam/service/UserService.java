package com.task.management.domain.iam.service;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.annotation.UseCase;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.iam.exception.EmailExistsException;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.domain.iam.port.in.GetUserProfileUseCase;
import com.task.management.domain.iam.port.in.command.RegisterUserCommand;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.iam.port.out.EncryptPasswordPort;
import com.task.management.domain.iam.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

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
    public UserProfile getUserProfile(UserId actorId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        return findOrThrow(actorId);
    }

    private UserProfile findOrThrow(UserId id) throws UseCaseException {
        return userRepositoryPort.findUserProfile(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("User not found"));
    }
}
