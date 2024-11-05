package io.spaceurgent.server.application.user.service;

import io.spaceurgent.server.application.user.exception.UserExistsException;
import io.spaceurgent.server.application.user.port.in.RegisterCommand;
import io.spaceurgent.server.application.user.port.out.HashPasswordPort;
import io.spaceurgent.server.application.user.port.out.UserRepository;
import io.spaceurgent.server.common.validation.ValidationService;
import io.spacurgent.server.domain.user.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidationService validationService;
    @Mock
    private HashPasswordPort hashPasswordPort;
    @InjectMocks
    private RegisterUserService registerUserService;
    @Test
    void register_ok() throws UserExistsException {
        final var command = createCommand();
        final var hashedPassword = "hashed";
        doReturn(false).when(userRepository).emailExists(eq(command.getEmail()));
        doReturn(hashedPassword).when(hashPasswordPort).hash(eq(command.getPassword()));
        doAnswer(invocation -> invocation.getArguments()[0]).when(userRepository).save(any());
        final var user = registerUserService.register(command);
        assertEquals(command.getEmail(), user.getEmail());
        assertEquals(command.getFirstName(), user.getFirstName());
        assertEquals(command.getLastName(), user.getLastName());
        assertTrue(user.passwordMatch(hashedPassword));
        assertEquals(Set.of(Role.USER), user.getRoles());
    }

    @Test
    void register_withExistingEmail_throws() {
        final var command = createCommand();
        doReturn(true).when(userRepository).emailExists(any());
        final var exception = assertThrows(UserExistsException.class, () -> registerUserService.register(command));
        assertEquals("User with email '%s' is registered".formatted(command.getEmail()), exception.getMessage());
    }

    private static RegisterCommand createCommand() {
        final var command = new RegisterCommand();
        command.setEmail("username@domain.com");
        command.setFirstName("Bob");
        command.setLastName("Test");
        command.setPassword("password".getBytes(StandardCharsets.UTF_8));
        return command;
    }
}