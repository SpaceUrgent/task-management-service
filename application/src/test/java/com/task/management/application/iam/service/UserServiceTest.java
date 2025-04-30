package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.Email;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.application.iam.port.out.EncryptPasswordPort;
import com.task.management.application.iam.port.out.UserCredentialsPort;
import com.task.management.application.iam.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private EncryptPasswordPort encryptPasswordPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserCredentialsPort userCredentialsPort;
    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldSaveNewUser_whenAllConditionsMet() throws EmailExistsException {
        final var encryptedPassword = "encryptedPassword";
        final var givenCommand = registerUserCommand();
        doReturn(false).when(userRepositoryPort).emailExists(eq(givenCommand.email()));
        doReturn(encryptedPassword).when(encryptPasswordPort).encrypt(eq(givenCommand.password()));
        final var userCaptor = ArgumentCaptor.forClass(User.class);
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepositoryPort).save(userCaptor.capture());
        userService.register(givenCommand);
        verify(userRepositoryPort).save(any());
        final var added = userCaptor.getValue();
        assertNotNull(added.getCreatedAt());
        assertEquals(givenCommand.email(), added.getEmail());
        assertEquals(givenCommand.firstName(), added.getFirstName());
        assertEquals(givenCommand.lastName(), added.getLastName());
        assertEquals(encryptedPassword, added.getEncryptedPassword());
    }

    @Test
    void register_shouldThrowEmailExistsException_whenEmailExists() {
        final var givenCommand = registerUserCommand();
        doReturn(true).when(userRepositoryPort).emailExists(eq(givenCommand.email()));
        assertThrows(EmailExistsException.class, () -> userService.register(givenCommand));
        verifyNoMoreInteractions(userRepositoryPort);
    }

    @Test
    void getUserProfile_shouldReturnUserProfile_whenAllConditionsMet() throws UseCaseException {
        final var expectedUserProfile = UserInfo.builder()
                .id(randomUserId())
                .email(new Email("user@mail.com"))
                .firstName("FName")
                .lastName("LName")
                .build();
        final var givenActorId = expectedUserProfile.id();
        doReturn(Optional.of(expectedUserProfile)).when(userRepositoryPort).find(eq(givenActorId));
        assertEquals(expectedUserProfile, userService.getUserProfile(givenActorId));
    }

    @Test
    void getUserProfile_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
        final var givenActorId = randomUserId();
        doReturn(Optional.empty()).when(userRepositoryPort).find(eq(givenActorId));
        assertThrows(UseCaseException.EntityNotFoundException.class, () -> userService.getUserProfile(givenActorId));
    }

    private UserId randomUserId() {
        return new UserId(new Random().nextLong());
    }

    private static RegisterUserCommand registerUserCommand() {
        return RegisterUserCommand.builder()
                .email(new Email("new-user@mail.com"))
                .firstName("FName")
                .lastName("LName")
                .password("password123456".toCharArray())
                .build();
    }
}