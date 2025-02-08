package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.port.out.FindProjectMemberPort;
import com.task.management.application.project.port.out.FindProjectUserByEmailPort;
import com.task.management.application.project.port.out.FindProjectUserByIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.task.management.application.project.service.ProjectTestUtils.randomProjectId;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUser;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProjectUserServiceTest {
    @Mock
    private FindProjectUserByIdPort findProjectUserByIdPort;
    @Mock
    private FindProjectUserByEmailPort findProjectUserByEmailPort;
    @Mock
    private FindProjectMemberPort findProjectMemberPort;
    @InjectMocks
    private ProjectUserService projectUserService;

    @Test
    void getProjectUserWithId_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenId = expectedUser.id();
        doReturn(Optional.of(expectedUser)).when(findProjectUserByIdPort).find(eq(givenId));
        assertEquals(expectedUser, projectUserService.getProjectUser(givenId));
    }

    @Test
    void getProjectUserWithId_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenId = expectedUser.id();
        doReturn(Optional.empty()).when(findProjectUserByIdPort).find(eq(givenId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getProjectUser(givenId)
        );
    }

    @Test
    void getProjectUserWithEmail_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomProjectUser();
        final var givenEmail = expectedUser.email();
        doReturn(Optional.of(expectedUser)).when(findProjectUserByEmailPort).find(eq(givenEmail));
        assertEquals(expectedUser, projectUserService.getProjectUser(givenEmail));
    }

    @Test
    void getProjectUserWithEmail_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var givenEmail = "random@email.com";
        doReturn(Optional.empty()).when(findProjectUserByEmailPort).find(eq(givenEmail));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectUserService.getProjectUser(givenEmail)
        );
    }

    @Test
    void isMember_shouldReturnTrue_whenMemberExists() {
        final var member = randomProjectUser();
        final var givenUserId = member.id();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.of(member)).when(findProjectMemberPort).findMember(eq(givenUserId), eq(givenProjectId));
        assertTrue(projectUserService.isMember(givenUserId, givenProjectId));
    }

    @Test
    void isMember_shouldReturnFalse_whenMemberDoesNotExist() {
        final var givenUserId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.empty()).when(findProjectMemberPort).findMember(eq(givenUserId), eq(givenProjectId));
        assertFalse(projectUserService.isMember(givenUserId, givenProjectId));
    }

    @Test
    void findProjectMember_shouldReturnOptionalOfProjectUser_whenAllConditionsMet() {
        final var expected = randomProjectUser();
        final var givenUserId = expected.id();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.of(expected)).when(findProjectMemberPort).findMember(eq(givenUserId), eq(givenProjectId));
        assertEquals(Optional.of(expected), projectUserService.findProjectMember(givenUserId, givenProjectId));
    }

    @Test
    void findProjectMember_shouldReturnEmptyOptionalOfProjectUser_whenProjectUserDoesNotExist() {
        final var givenUserId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.empty()).when(findProjectMemberPort).findMember(eq(givenUserId), eq(givenProjectId));
        assertTrue(projectUserService.findProjectMember(givenUserId, givenProjectId).isEmpty());
    }
}