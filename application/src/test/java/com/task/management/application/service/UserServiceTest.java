package com.task.management.application.service;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.model.User;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.management.application.port.out.PasswordEncryptor;
import com.task.management.application.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncryptor passwordEncryptor;
    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldCreateNewUser_whenAllConditionsMet() throws EmailExistsException {
        final var registerDto = getRegisterUserDto();
        final var encryptedPassword = "encryptedPassword";

        when(userRepository.emailExists(registerDto.getEmail())).thenReturn(false);
        when(passwordEncryptor.encrypt(registerDto.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.add(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(registerDto);

        assertNotNull(result);
        assertEquals(registerDto.getEmail(), result.getEmail());
        assertEquals(registerDto.getFirstName(), result.getFirstName());
        assertEquals(registerDto.getLastName(), result.getLastName());
        assertEquals(encryptedPassword, result.getEncryptedPassword());

        verify(validationService, times(1)).validate(registerDto);
    }

    @Test
    void register_shouldThrowEmailExistsException_whenUserWithGivenExists() throws EmailExistsException {
        final var registerDto = getRegisterUserDto();

        when(userRepository.emailExists(registerDto.getEmail())).thenReturn(true);

        final var exception = assertThrows(
                EmailExistsException.class,
                () -> userService.register(registerDto)
        );
        assertEquals("User with email '%s' exists".formatted(registerDto.getEmail()), exception.getMessage());
    }

    private static RegisterUserDto getRegisterUserDto() {
        final var registerDto = new RegisterUserDto();
        registerDto.setEmail("test@example.com");
        registerDto.setFirstName("John Doe");
        registerDto.setLastName("Doe");
        registerDto.setPassword("password123".toCharArray());
        return registerDto;
    }
}