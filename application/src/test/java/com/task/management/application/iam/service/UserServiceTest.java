package com.task.management.application.iam.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.iam.CurrentPasswordMismatchException;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.command.UpdatePasswordCommand;
import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.common.model.objectvalue.UserId;
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

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        doReturn(Optional.of(expectedUserProfile)).when(userRepositoryPort).findUserInfo(eq(givenActorId));
        assertEquals(expectedUserProfile, userService.getUserProfile(givenActorId));
    }

    @Test
    void getUserProfile_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
        final var givenActorId = randomUserId();
        doReturn(Optional.empty()).when(userRepositoryPort).findUserInfo(eq(givenActorId));
        assertThrows(UseCaseException.EntityNotFoundException.class, () -> userService.getUserProfile(givenActorId));
    }

    @Test
    void updateName_shouldSaveUpdatedUser_whenAllConditionsMet() throws UseCaseException {
        final var user = randomUser();
        final var givenActorId = user.getId();
        final var givenCommand = updateNameCommand();
        doReturn(Optional.of(user)).when(userRepositoryPort).find(eq(givenActorId));

        userService.updateName(givenActorId, givenCommand);

        verify(userRepositoryPort).save(argThat(saved -> {
            assertNotNull(saved.getUpdatedAt());
            assertEquals(givenCommand.firstName(), saved.getFirstName());
            assertEquals(givenCommand.lastName(), saved.getLastName());
            return true;
        }));
    }

    @Test
    void updateName_shouldThrowEntityNotFoundException_whenUserNotFound() {
        final var givenActorId = randomUserId();
        final var givenCommand = updateNameCommand();
        doReturn(Optional.empty()).when(userRepositoryPort).find(eq(givenActorId));

        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> userService.updateName(givenActorId, givenCommand)
        );

        verify(userRepositoryPort, times(0)).save(any());
    }

    @Test
    void updatePassword_shouldSaveUpdatedUser_whenAllConditionsMet() throws UseCaseException {
        final var user = randomUser();
        final var givenActorId = user.getId();
        final var givenCommand = updatePasswordCommand();
        final var expectedNewEncryptedPassword = "New encrypted password";

        doReturn(Optional.of(user)).when(userRepositoryPort).find(eq(givenActorId));
        doReturn(user.getEncryptedPassword()).when(encryptPasswordPort).encrypt(eq(givenCommand.currentPassword()));
        doReturn(expectedNewEncryptedPassword).when(encryptPasswordPort).encrypt(eq(givenCommand.newPassword()));

        userService.updatePassword(givenActorId, givenCommand);

        verify(userRepositoryPort).save(argThat(saved -> {
            assertNotNull(saved.getUpdatedAt());
            assertEquals(expectedNewEncryptedPassword, saved.getEncryptedPassword());
            return true;
        }));
    }

    @Test
    void updatePassword_shouldThrowEntityNotFoundException_whenUserNotFound() {
        final var givenActorId = randomUserId();
        final var givenCommand = updatePasswordCommand();

        doReturn(Optional.empty()).when(userRepositoryPort).find(eq(givenActorId));

        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> userService.updatePassword(givenActorId, givenCommand)
        );

        verify(userRepositoryPort, times(0)).save(any());
    }

    @Test
    void updatePassword_shouldThrowIllegalAccessException_whenOldPasswordMismatches() {
        final var user = randomUser();
        final var givenActorId = user.getId();
        final var givenCommand = updatePasswordCommand();

        doReturn(Optional.of(user)).when(userRepositoryPort).find(eq(givenActorId));
        doReturn("Not matching").when(encryptPasswordPort).encrypt(eq(givenCommand.currentPassword()));

        assertThrows(
                CurrentPasswordMismatchException.class,
                () -> userService.updatePassword(givenActorId, givenCommand)
        );
        verify(userRepositoryPort, times(0)).save(any());
    }

    private UserId randomUserId() {
        return new UserId(new Random().nextLong());
    }

    private User randomUser() {
        return User.builder()
                .id(randomUserId())
                .createdAt(Instant.now())
                .email(new Email("username@domain.com"))
                .firstName("John")
                .lastName("Dow")
                .encryptedPassword("encryptedPassword")
                .build();
    }

    private static RegisterUserCommand registerUserCommand() {
        return RegisterUserCommand.builder()
                .email(new Email("new-user@mail.com"))
                .firstName("FName")
                .lastName("LName")
                .password("password123456".toCharArray())
                .build();
    }

    private static UpdateNameCommand updateNameCommand() {
        return UpdateNameCommand.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .build();
    }

    private static UpdatePasswordCommand updatePasswordCommand() {
        return UpdatePasswordCommand.builder()
                .currentPassword("old password".toCharArray())
                .newPassword("new password".toCharArray())
                .build();
    }
}