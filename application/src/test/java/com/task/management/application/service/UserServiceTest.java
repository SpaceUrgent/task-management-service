package com.task.management.application.service;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.exception.UserNotFoundException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.management.application.port.out.AddUserPort;
import com.task.management.application.port.out.EmailExistsPort;
import com.task.management.application.port.out.EncryptPasswordPort;
import com.task.management.application.port.out.FindUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String EMAIL = "test@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final char[] PASSWORD = "password123".toCharArray();
    private static final String ENCRYPTED_PASSWORD = "encryptedPassword";

    @Mock
    private ValidationService validationService;
    @Mock
    private EncryptPasswordPort passwordEncryptor;
    @Mock
    private EmailExistsPort emailExistsPort;
    @Mock
    private AddUserPort addUserPort;
    @Mock
    private FindUserPort findUserPort;
    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldCreateNewUser_whenAllConditionsMet() throws EmailExistsException {
        final var registerDto = getRegisterUserDto();

        when(emailExistsPort.emailExists(registerDto.getEmail())).thenReturn(false);
        when(passwordEncryptor.encrypt(registerDto.getPassword())).thenReturn(ENCRYPTED_PASSWORD);
        when(addUserPort.add(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(registerDto);

        assertNotNull(result);
        assertEquals(registerDto.getEmail(), result.getEmail());
        assertEquals(registerDto.getFirstName(), result.getFirstName());
        assertEquals(registerDto.getLastName(), result.getLastName());
        assertEquals(ENCRYPTED_PASSWORD, result.getEncryptedPassword());

        verify(validationService, times(1)).validate(registerDto);
    }

    @Test
    void register_shouldThrowEmailExistsException_whenUserWithGivenExists() throws EmailExistsException {
        final var registerDto = getRegisterUserDto();
        when(emailExistsPort.emailExists(registerDto.getEmail())).thenReturn(true);
        final var exception = assertThrows(
                EmailExistsException.class,
                () -> userService.register(registerDto)
        );
        assertEquals("User with email '%s' exists".formatted(registerDto.getEmail()), exception.getMessage());
    }

    @Test
    void getUser_shouldReturnUser_whenAllConditionsMet() throws UserNotFoundException {
        final var expectedUser = getUser();
        doReturn(Optional.of(expectedUser)).when(findUserPort).findById(eq(expectedUser.getId()));
        assertEquals(expectedUser, userService.getUser(expectedUser.getId()));
    }

    @Test
    void getUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() throws UserNotFoundException {
        final var givenUserId = randomUserId();
        doReturn(Optional.empty()).when(findUserPort).findById(eq(givenUserId));
        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(givenUserId)
        );
        assertNotNull(exception.getMessage());
    }

    private static User getUser() {
        return User.builder()
                .id(randomUserId())
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .build();
    }

    private static UserId randomUserId() {
        return new UserId(new Random().nextLong());
    }

    private static RegisterUserDto getRegisterUserDto() {
        final var registerDto = new RegisterUserDto();
        registerDto.setEmail(EMAIL);
        registerDto.setFirstName(FIRST_NAME);
        registerDto.setLastName(LAST_NAME);
        registerDto.setPassword(PASSWORD);
        return registerDto;
    }
}