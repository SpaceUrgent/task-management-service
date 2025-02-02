package com.task.management.application.service;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.dto.RegisterUserDto;
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

import static com.task.management.application.service.TestUtils.EMAIL;
import static com.task.management.application.service.TestUtils.ENCRYPTED_PASSWORD;
import static com.task.management.application.service.TestUtils.FIRST_NAME;
import static com.task.management.application.service.TestUtils.LAST_NAME;
import static com.task.management.application.service.TestUtils.PASSWORD;
import static com.task.management.application.service.TestUtils.getTestUser;
import static com.task.management.application.service.TestUtils.randomLong;
import static com.task.management.application.service.TestUtils.randomUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
        final var expectedId = randomLong();
        final var expectedUserDTO = UserDTO.builder()
                .id(expectedId)
                .email(registerDto.getEmail())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .build();

        when(emailExistsPort.emailExists(registerDto.getEmail())).thenReturn(false);
        when(passwordEncryptor.encrypt(registerDto.getPassword())).thenReturn(ENCRYPTED_PASSWORD);
        when(addUserPort.add(any())).thenAnswer(invocation -> {
            var argument = (User) invocation.getArgument(0);
            return User.builder()
                    .id(new UserId(expectedId))
                    .email(argument.getEmail())
                    .firstName(argument.getFirstName())
                    .lastName(argument.getLastName())
                    .encryptedPassword(argument.getEncryptedPassword())
                    .build();
        });

        UserDTO result = userService.register(registerDto);
        assertEquals(expectedUserDTO, result);

        verify(validationService, times(1)).validate(registerDto);
    }

    @Test
    void register_shouldThrowEmailExistsException_whenUserWithGivenEmailExists() {
        final var registerDto = getRegisterUserDto();
        when(emailExistsPort.emailExists(registerDto.getEmail())).thenReturn(true);
        final var exception = assertThrows(
                EmailExistsException.class,
                () -> userService.register(registerDto)
        );
        assertEquals("User with email '%s' exists".formatted(registerDto.getEmail()), exception.getMessage());
    }

    @Test
    void getUser_shouldReturnUser_whenUserWithGivenIdExists() throws EntityNotFoundException {
        final var existingUser = getTestUser();
        final var expectedUserDTO = UserDTO.builder()
                .id(existingUser.getId().value())
                .email(existingUser.getEmail())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .build();
        doReturn(Optional.of(existingUser)).when(findUserPort).findById(eq(existingUser.getId()));
        assertEquals(expectedUserDTO, userService.getUser(existingUser.getId()));
    }

    @Test
    void getUser_shouldThrowEntityNotFoundException_whenUserWithGivenIdDoesNotExist() {
        final var givenUserId = randomUserId();
        doReturn(Optional.empty()).when(findUserPort).findById(eq(givenUserId));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUser(givenUserId)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void getUser_shouldReturnUser_whenUserWithGivenEmailExists() throws EntityNotFoundException {
        final var existingUser = getTestUser();
        final var expectedUserDTO = UserDTO.builder()
                .id(existingUser.getId().value())
                .email(existingUser.getEmail())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .build();
        doReturn(Optional.of(existingUser)).when(findUserPort).findByEmail(eq(existingUser.getEmail()));
        assertEquals(expectedUserDTO, userService.getUser(existingUser.getEmail()));
    }

    @Test
    void getUser_shouldThrowEntityNotFoundException_whenUserWithGivenEmailDoesNotExist() {
        doReturn(Optional.empty()).when(findUserPort).findByEmail(eq(EMAIL));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUser(EMAIL)
        );
        assertNotNull(exception.getMessage());
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