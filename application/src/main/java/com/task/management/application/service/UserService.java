package com.task.management.application.service;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.exception.UserNotFoundException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.management.application.port.out.PasswordEncryptor;
import com.task.management.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase,
                                    GetUserUseCase {
    private final ValidationService validationService;
    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public User register(final RegisterUserDto registerUserDto) throws EmailExistsException {
        validationService.validate(registerUserDto);
        final var email = registerUserDto.getEmail();
        if (userRepository.emailExists(email)) {
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
        return userRepository.add(user);
    }

    @Override
    public User getUser(final UserId id) throws UserNotFoundException {
        requireNonNull(id, "User id is required");
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
