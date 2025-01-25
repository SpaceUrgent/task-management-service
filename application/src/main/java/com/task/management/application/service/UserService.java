package com.task.management.application.service;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.management.application.port.out.AddUserPort;
import com.task.management.application.port.out.EmailExistsPort;
import com.task.management.application.port.out.EncryptPasswordPort;
import com.task.management.application.port.out.FindUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.task.management.application.service.ValidationService.userIdRequired;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase,
                                    GetUserUseCase {
    private final ValidationService validationService;
    private final EncryptPasswordPort passwordEncryptor;
    private final EmailExistsPort emailExistsPort;
    private final AddUserPort addUserPort;
    private final FindUserPort findUserPort;

    @Override
    public User register(final RegisterUserDto registerUserDto) throws EmailExistsException {
        validationService.validate(registerUserDto);
        final var email = registerUserDto.getEmail();
        if (emailExistsPort.emailExists(email)) {
            log.debug("register(): user with email '{}' exists", email);
            throw new EmailExistsException("User with email '%s' exists".formatted(email));
        }
        final var encryptedPassword = passwordEncryptor.encrypt(registerUserDto.getPassword());
        var user = User.builder()
                .email(email)
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .encryptedPassword(encryptedPassword)
                .build();
        return addUserPort.add(user);
    }

    @Override
    public User getUser(final UserId id) throws EntityNotFoundException {
        userIdRequired(id);
        return findByIdOrThrow(id);
    }

    public User getUser(final String email) throws EntityNotFoundException {
        requireNonNull(email, "Email is required");
        return findByEmailOrThrow(email);
    }

    private User findByIdOrThrow(UserId id) throws EntityNotFoundException {
        return findUserPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private User findByEmailOrThrow(String email) throws EntityNotFoundException {
        return findUserPort.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email '%s' not found".formatted(email)));
    }
}
