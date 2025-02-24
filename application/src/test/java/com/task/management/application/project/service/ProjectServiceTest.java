package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.port.in.command.UpdateProjectCommand;
import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.port.out.AddProjectMemberPort;
import com.task.management.application.project.port.out.FindProjectByIdPort;
import com.task.management.application.project.port.out.FindProjectMembersPort;
import com.task.management.application.project.port.out.FindProjectsByMemberPort;
import com.task.management.application.project.port.out.AddProjectPort;
import com.task.management.application.project.port.in.command.CreateProjectCommand;
import com.task.management.application.project.port.out.UpdateProjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.task.management.application.project.service.ProjectTestUtils.getProjectUserAnswer;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectId;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUser;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUserId;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUsers;
import static com.task.management.application.project.service.ProjectTestUtils.self;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectUserService projectUserService;
    @Mock
    private AddProjectPort addProjectPort;
    @Mock
    private UpdateProjectPort updateProjectPort;
    @Mock
    private FindProjectByIdPort findProjectByIdPort;
    @Mock
    private FindProjectMembersPort findProjectMembersPort;
    @Mock
    private AddProjectMemberPort addProjectMemberPort;
    @Mock
    private FindProjectsByMemberPort findProjectsByMemberPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var command = createProjectCommand();
        final var givenActorId = randomProjectUserId();
        doAnswer(getProjectUserAnswer()).when(projectUserService).getProjectUser(eq(givenActorId));
        doAnswer(self(Project.class)).when(addProjectPort).add(any());
        final var created = projectService.createProject(givenActorId, command);
        assertNotNull(created.getCreatedAt());
        assertEquals(command.title(), created.getTitle());
        assertEquals(command.description(), created.getDescription());
    }

    @Test
    void getAvailableProjects_shouldReturnProjectList() {
        final var expectedProjects = randomProjectPreviews();
        final var givenActorId = randomProjectUserId();
        doReturn(expectedProjects).when(findProjectsByMemberPort).findProjectsByMember(eq(givenActorId));
        assertEquals(expectedProjects, projectService.getAvailableProjects(givenActorId));
    }

    @Test
    void getMembers_shouldReturnMembers_whenAllConditionsMet() throws UseCaseException {
        final var expected = randomProjectUsers();
        final var givenProjectId = randomProjectId();
        final var givenActorId = randomProjectUserId();
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(expected).when(findProjectMembersPort).findMembers(eq(givenProjectId));
        assertEquals(expected, projectService.getMembers(givenActorId, givenProjectId));
    }

    @Test
    void getMembers_shouldThrowIllegalAccessException_whenCurrentUserIsNotProjectMember() {
        final var givenProjectId = randomProjectId();
        final var givenActorId = randomProjectUserId();
        doReturn(false).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.getMembers(givenActorId, givenProjectId)
        );
    }

    @Test
    void getProject_shouldReturnProject_whenAllConditionsMet() throws UseCaseException {
        final var expectedProject = randomProject();
        final var givenActorId = expectedProject.getOwner().id();
        final var givenProjectId = expectedProject.getId();
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(Optional.of(expectedProject)).when(findProjectByIdPort).find(eq(givenProjectId));
        assertEquals(expectedProject, projectService.getProject(givenActorId, givenProjectId));
    }

    @Test
    void getProject_shouldThrowIllegalAccessException_whenUserIsNotProjectMember() {
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        doReturn(false).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.getProject(givenActorId, givenProjectId)
        );
    }

    @Test
    void getProject_shouldThrowEntityNotFoundException_whenUserIsNotProjectMember() {
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(Optional.empty()).when(findProjectByIdPort).find(eq(givenProjectId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectService.getProject(givenActorId, givenProjectId)
        );
    }

    @Test
    void updateProject_shouldReturnUpdated_whenAllConditionsMet() throws UseCaseException {
        var project = randomProject();
        final var givenCommand = updateProjectCommand(project);
        final var expectedProject = Project.builder()
                .id(project.getId())
                .createdAt(project.getCreatedAt())
                .title(givenCommand.title())
                .description(givenCommand.description())
                .owner(project.getOwner())
                .build();
        final var givenActorId = project.getOwner().id();
        doReturn(Optional.of(project)).when(findProjectByIdPort).find(eq(project.getId()));
        doAnswer(self(Project.class)).when(updateProjectPort).update(any());
        assertEquals(expectedProject, projectService.updateProject(givenActorId, givenCommand));
    }

    @Test
    void updateProject_shouldThrowEntityNotFoundException_whenAllConditionsMet() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand(project);
        final var givenActorId = project.getOwner().id();
        doReturn(Optional.empty()).when(findProjectByIdPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectService.updateProject(givenActorId, givenCommand)
        );
        verifyNoInteractions(updateProjectPort);
    }

    @Test
    void updateProject_shouldThrowIllegalAccessException_whenAllConditionsMet() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand(project);
        final var givenActorId = randomProjectUserId();
        doReturn(Optional.of(project)).when(findProjectByIdPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateProject(givenActorId, givenCommand)
        );
        verifyNoInteractions(updateProjectPort);
    }

    @Test
    void addMember_shouldAddMember_whenAllConditionsMet() throws Exception {
        final var member = randomProjectUser();
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = "username@domain.com";
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        doReturn(member).when(projectUserService).getProjectUser(eq(givenEmail));
        projectService.addMember(givenActorId, givenProjectId, givenEmail);
        verify(addProjectMemberPort).addMember(eq(givenProjectId), eq(member.id()));
    }

    @Test
    void addMember_shouldThrowIllegalAccessException_whenCurrentUserIsNotProjectMember() {
        final var givenActorId = randomProjectUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = "username@domain.com";
        doReturn(false).when(projectUserService).isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.addMember(givenActorId, givenProjectId, givenEmail)
        );
        verifyNoMoreInteractions(addProjectMemberPort);
    }

    private List<Project> randomProjectPreviews() {
        return IntStream.range(0, 10)
                .mapToObj(value -> randomProject())
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
        final var owner = ProjectUser.withId(randomProjectUserId());
        return Project.builder()
                .id(projectId)
                .createdAt(Instant.now())
                .title("Title %d".formatted(projectId.value()))
                .description("Description %d".formatted(projectId.value()))
                .owner(owner)
                .build();
    }

    private CreateProjectCommand createProjectCommand() {
        return CreateProjectCommand.builder()
                .title("New project title")
                .description("New project description")
                .build();
    }

    private static UpdateProjectCommand updateProjectCommand(Project project) {
        return UpdateProjectCommand.builder()
                .projectId(project.getId())
                .title("Update title")
                .description("Updated description")
                .build();
    }
}