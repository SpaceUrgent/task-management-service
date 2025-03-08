package com.task.management.domain.project.service;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.domain.project.port.in.command.UpdateProjectCommand;
import com.task.management.domain.project.port.in.command.CreateProjectCommand;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.task.management.domain.project.service.ProjectTestUtils.randomProjectId;
import static com.task.management.domain.project.service.ProjectTestUtils.randomProjectUser;
import static com.task.management.domain.project.service.ProjectTestUtils.randomProjectUserId;
import static com.task.management.domain.project.service.ProjectTestUtils.randomProjectUsers;
import static com.task.management.domain.project.service.ProjectTestUtils.self;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectUserService projectUserService;
    @Mock
    private ProjectRepositoryPort projectRepositoryPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var command = createProjectCommand();
        final var givenActorId = randomProjectUserId();
        final var projectCaptor = ArgumentCaptor.forClass(Project.class);
        doAnswer(self(Project.class)).when(projectRepositoryPort).save(projectCaptor.capture());
        projectService.createProject(givenActorId, command);
        final var created = projectCaptor.getValue();
        assertNotNull(created.getCreatedAt());
        assertEquals(command.title(), created.getTitle());
        assertEquals(command.description(), created.getDescription());
    }

    @Test
    void getAvailableProjects_shouldReturnProjectList() {
        final var expectedProjects = randomProjectPreviews();
        final var givenActorId = randomProjectUserId();
        doReturn(expectedProjects).when(projectRepositoryPort).findProjectsByMember(eq(givenActorId));
        assertEquals(expectedProjects, projectService.getAvailableProjects(givenActorId));
    }

    @Test
    void getMembers_shouldReturnMembers_whenAllConditionsMet() throws UseCaseException {
        final var expected = randomProjectUsers();
        final var givenProjectId = randomProjectId();
        final var givenActorId = randomProjectUserId();
        doReturn(true).when(projectRepositoryPort).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(expected).when(projectRepositoryPort).findMembers(eq(givenProjectId));
        assertEquals(expected, projectService.getMembers(givenActorId, givenProjectId));
    }

    @Test
    void getMembers_shouldThrowIllegalAccessException_whenCurrentUserIsNotProjectMember() {
        final var givenProjectId = randomProjectId();
        final var givenActorId = randomProjectUserId();
        doReturn(false).when(projectRepositoryPort).isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.getMembers(givenActorId, givenProjectId)
        );
    }

    @Test
    void updateProject_shouldReturnUpdated_whenAllConditionsMet() throws UseCaseException {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = project.getOwnerId();
        final var projectCaptor = ArgumentCaptor.forClass(Project.class);
        doReturn(Optional.of(project)).when(projectRepositoryPort).find(eq(project.getId()));
        doAnswer(self(Project.class)).when(projectRepositoryPort).save(projectCaptor.capture());
        projectService.updateProject(givenActorId, project.getId(), givenCommand);
        final var saved = projectCaptor.getValue();
        assertNotNull(saved.getUpdatedAt());
        assertEquals(givenCommand.title(), saved.getTitle());
        assertEquals(givenCommand.description(), saved.getDescription());
    }

    @Test
    void updateProject_shouldThrowEntityNotFoundException_whenAllConditionsMet() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = project.getOwnerId();
        doReturn(Optional.empty()).when(projectRepositoryPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectService.updateProject(givenActorId, project.getId(), givenCommand)
        );
        verify(projectRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateProject_shouldThrowIllegalAccessException_whenAllConditionsMet() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = randomProjectUserId();
        doReturn(Optional.of(project)).when(projectRepositoryPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateProject(givenActorId, project.getId(), givenCommand)
        );
        verify(projectRepositoryPort, times(0)).save(any());
    }

    @Test
    void addMember_shouldAddMember_whenAllConditionsMet() throws Exception {
        final var member = randomProjectUser();
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = new Email("username@domain.com");
        doReturn(true).when(projectRepositoryPort).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(member).when(projectUserService).getProjectUser(eq(givenEmail));
        projectService.addMember(givenActorId, givenProjectId, givenEmail);
        verify(projectRepositoryPort).addMember(eq(givenProjectId), eq(member.id()));
    }

    @Test
    void addMember_shouldThrowIllegalAccessException_whenCurrentUserIsNotProjectMember() {
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = new Email ("username@domain.com");
        doReturn(false).when(projectRepositoryPort).isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.addMember(givenActorId, givenProjectId, givenEmail)
        );
        verify(projectRepositoryPort, times(0)).addMember(any(), any());
    }

    private List<ProjectPreview> randomProjectPreviews() {
        return IntStream.range(0, 10)
                .mapToObj(value -> rendomProjectPreview())
                .toList();
    }

    private static ProjectPreview rendomProjectPreview() {
        final var projectId = randomProjectId();
        final var projectIdValue = projectId.value();
        return ProjectPreview.builder()
                .id(projectId)
                .title("Title %d".formatted(projectIdValue))
                .title("Description %d".formatted(projectIdValue))
                .owner(randomProjectUser())
                .build();
    }

    private static Project randomProject() {
        final var projectId = randomProjectId();
        return Project.builder()
                .id(projectId)
                .createdAt(Instant.now())
                .title("Title %d".formatted(projectId.value()))
                .description("Description %d".formatted(projectId.value()))
                .ownerId(randomProjectUserId())
                .build();
    }

    private CreateProjectCommand createProjectCommand() {
        return CreateProjectCommand.builder()
                .title("New project title")
                .description("New project description")
                .build();
    }

    private static UpdateProjectCommand updateProjectCommand() {
        return UpdateProjectCommand.builder()
                .title("Update title")
                .description("Updated description")
                .build();
    }
}