package com.task.management.domain.project.service;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.port.out.ProjectUserRepositoryPort;
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
    private ProjectUserRepositoryPort projectUserRepositoryPort;
    @InjectMocks
    private ProjectUserService projectUserService;

    @Test
    void getProjectUserWithId_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenId = expectedUser.id();
        doReturn(Optional.of(expectedUser)).when(projectUserRepositoryPort).find(eq(givenId));
        assertEquals(expectedUser, projectUserService.getProjectUser(givenId));
    }

    @Test
    void getProjectUserWithId_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenId = expectedUser.id();
        doReturn(Optional.empty()).when(projectUserRepositoryPort).find(eq(givenId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getProjectUser(givenId)
        );
    }

    @Test
    void getProjectUserWithEmail_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenEmail = expectedUser.email();
        doReturn(Optional.of(expectedUser)).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertEquals(expectedUser, projectUserService.getProjectUser(givenEmail));
    }

    @Test
    void getProjectUserWithEmail_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var givenEmail = new Email("random@email.com");
        doReturn(Optional.empty()).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getProjectUser(givenEmail)
        );
    }
}