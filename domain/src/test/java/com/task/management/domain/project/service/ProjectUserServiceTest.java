package com.task.management.domain.project.service;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.project.application.service.UserService;
import com.task.management.domain.project.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProjectUserServiceTest {
    @Mock
    private UserRepositoryPort projectUserRepositoryPort;
    @InjectMocks
    private UserService projectUserService;

    @Test
    void getProjectUserWithId_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomUserInfo();
        final var givenId = expectedUser.id();
        doReturn(Optional.of(expectedUser)).when(projectUserRepositoryPort).find(eq(givenId));
        assertEquals(expectedUser, projectUserService.getUser(givenId));
    }

    @Test
    void getProjectUserWithId_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var expectedUser = ProjectTestUtils.randomMemberView();
        final var givenId = expectedUser.id();
        doReturn(Optional.empty()).when(projectUserRepositoryPort).find(eq(givenId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getUser(givenId)
        );
    }

    @Test
    void getProjectUserWithEmail_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomUserInfo();
        final var givenEmail = expectedUser.email();
        doReturn(Optional.of(expectedUser)).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertEquals(expectedUser, projectUserService.getUser(givenEmail));
    }

    @Test
    void getProjectUserWithEmail_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var givenEmail = new Email("random@email.com");
        doReturn(Optional.empty()).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getUser(givenEmail)
        );
    }
}