package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.iam.exception.EmailExistsException;
import com.task.management.application.iam.model.UserId;
import com.task.management.application.iam.model.UserProfile;
import com.task.management.application.iam.port.in.GetUserProfileUseCase;
import com.task.management.application.iam.port.in.command.RegisterUserCommand;
import com.task.management.application.iam.model.User;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.application.iam.port.out.AddUserPort;
import com.task.management.application.iam.port.out.EmailExistsPort;
import com.task.management.application.iam.port.out.FindUserProfileByIdPort;
import com.task.management.application.iam.port.out.EncryptPasswordPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;


@Slf4j
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase,
                                    GetUserProfileUseCase {
    private final ValidationService validationService;
    private final EncryptPasswordPort encryptPasswordPort;
    private final EmailExistsPort emailExistsPort;
    private final AddUserPort saveUserPort;
    private final FindUserProfileByIdPort findUserByIdPort;

    @Override
    public void register(final RegisterUserCommand command) throws EmailExistsException {
        validationService.validate(command);
        final var email = command.email();
        if (emailExistsPort.emailExists(email)) {
            log.debug("Register failed, user with email '{}' exists", email);
            throw new EmailExistsException("User with email '%s' exists".formatted(email));
        }
        final var encryptedPassword = encryptPasswordPort.encrypt(command.password());
        var user = User.builder()
                .createdAt(Instant.now())
                .email(email)
                .firstName(command.firstName())
                .lastName(command.lastName())
                .encryptedPassword(encryptedPassword)
                .build();
        saveUserPort.add(user);
    }

    @Override
    public UserProfile getUserProfile(UserId actorId) throws UseCaseException {
        return findOrThrow(actorId);
    }

    private UserProfile findOrThrow(UserId id) throws UseCaseException {
        return findUserByIdPort.find(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", id);
                    return new UseCaseException.EntityNotFoundException("User not found");
                });
    }
}
