package com.task.management.application.service;

import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.task.management.application.service.TestUtils.EMAIL;
import static com.task.management.application.service.TestUtils.getTestUser;
import static com.task.management.application.service.TestUtils.randomLong;
import static com.task.management.application.service.TestUtils.randomUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private final static String PROJECT_TITLE = "New Project";
    private final static String PROJECT_DESCRIPTION = "Project description";

    @Mock
    private ValidationService validationService;
    @Mock
    private AddProjectPort projectRepository;
    @Mock
    private UserService userService;
    @Mock
    private FindProjectPort findProjectPort;
    @Mock
    private AddProjectMemberPort addProjectMemberPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() {
        final var givenUserId = new UserId(10L);
        final var givenCreateProjectDto = getCreateProjectDto();
        doAnswer(invocation -> invocation.getArgument(0)).when(projectRepository).add(any());
        final var created = projectService.createProject(givenUserId, givenCreateProjectDto);
        assertEquals(givenCreateProjectDto.getTitle(), created.getTitle());
        assertEquals(givenCreateProjectDto.getDescription(), created.getDescription());
        assertEquals(givenUserId, created.getOwner());
        assertEquals(Set.of(givenUserId), created.getMembers());
    }

    @Test
    void addMember_shouldAddMember_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var project = Project.builder()
                .id(randomProjectId())
                .owner(givenCurrentUserId)
                .members(Set.of(givenCurrentUserId))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        final var givenProjectId = project.getId();
        final var expectedMember = getTestUser();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        doReturn(expectedMember).when(userService).getUser(eq(EMAIL));
        projectService.addMember(givenCurrentUserId, project.getId(), EMAIL);
        verify(addProjectMemberPort).addMember(eq(givenProjectId), eq(expectedMember.getId()));
    }

    @Test
    void addMember_shouldThrowEntityNotFoundException_whenProjectDoesNotExists() {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.empty()).when(findProjectPort).findById(eq(givenProjectId));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.addMember(givenCurrentUserId, givenProjectId, EMAIL)
        );
        assertEquals("Project not found", exception.getMessage());
        verifyNoInteractions(addProjectMemberPort);
    }

    @Test
    void addMember_shouldThrowEntityNotFoundException_whenUserByEmailNotFound() throws EntityNotFoundException {
        final var expectedErrorMessage = "User by email not found";
        final var givenCurrentUserId = randomUserId();
        final var project = Project.builder()
                .id(randomProjectId())
                .owner(givenCurrentUserId)
                .members(Set.of(givenCurrentUserId))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        final var givenProjectId = project.getId();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        doThrow(new EntityNotFoundException(expectedErrorMessage)).when(userService).getUser(eq(EMAIL));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.addMember(givenCurrentUserId, givenProjectId, EMAIL)
        );
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void addMember_shouldThrowInsufficientPrivilegesException_whenCurrentUserIsNotProjectMember() {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var project = Project.builder()
                .id(givenProjectId)
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .owner(randomUserId())
                .members(Set.of(randomUserId()))
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.addMember(givenCurrentUserId, givenProjectId, EMAIL)
        );
        assertEquals("Current user is not allowed to add project member", exception.getMessage());
        verifyNoInteractions(addProjectMemberPort);
    }

    private static CreateProjectDto getCreateProjectDto() {
        var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }
}