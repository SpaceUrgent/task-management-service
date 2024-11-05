package io.spaceurgent.server.application.user.service;

import io.spaceurgent.server.application.user.exception.UserExistsException;
import io.spaceurgent.server.application.user.port.in.RegisterCommand;
import io.spaceurgent.server.application.user.port.in.RegisterUserUseCase;
import io.spaceurgent.server.application.user.port.out.HashPasswordPort;
import io.spaceurgent.server.application.user.port.out.UserRepository;
import io.spaceurgent.server.common.validation.ValidationService;
import io.spacurgent.server.domain.user.Role;
import io.spacurgent.server.domain.user.User;

import java.util.Set;

public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final HashPasswordPort hashPasswordPort;
    private final ValidationService validationService;

    public RegisterUserService(UserRepository userRepository,
                               HashPasswordPort hashPasswordPort,
                               ValidationService validationService) {
        this.userRepository = userRepository;
        this.hashPasswordPort = hashPasswordPort;
        this.validationService = validationService;
    }

    @Override
    public User register(RegisterCommand command) throws UserExistsException {
        validationService.validate(command);
        if (userRepository.emailExists(command.getEmail())) {
            throw new UserExistsException("User with email '%s' is registered".formatted(command.getEmail()));
        }
        final var user = User.builder()
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .hashedPassword(hashPasswordPort.hash(command.getPassword()))
                .roles(Set.of(Role.USER))
                .build();
        return userRepository.save(user);
    }
}
