package com.task.management.application.service;

import com.task.management.application.common.PageQuery;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.in.dto.UpdateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import com.task.management.application.port.out.UpdateProjectPort.UpdateProjectCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @Mock
    private FindProjectsByMemberPort findProjectsByMemberPort;
    @Mock
    private FindProjectDetailsPort findProjectDetailsPort;
    @Mock
    private UpdateProjectPort updateProjectPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void getAvailableProjects_shouldReturnProjectList() {
        final var givenPage = new PageQuery(1, 10);
        final var givenUserId = randomUserId();
        final var expectedProjects = randomProjectsWithMember(givenPage.getPageSize(), givenUserId);
        doReturn(expectedProjects).when(findProjectsByMemberPort).findProjectsByMember(eq(givenUserId), eq(givenPage));
        assertEquals(expectedProjects, projectService.getAvailableProjects(givenUserId, givenPage));
    }

    @Test
    void getProjectDetails_shouldReturnProjectDetails_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var user = getTestUser();
        final var givenCurrentUser = user.getId();
        final var givenProjectId = randomProjectId();
        final var owner = ProjectUser.withId(givenCurrentUser);
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(owner)
                .members(Set.of(owner))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        final var expectedProjectDetails = new ProjectDetails(project, user, List.of(user));
        doReturn(Optional.of(expectedProjectDetails)).when(findProjectDetailsPort).findProjectDetails(eq(givenProjectId));
        assertEquals(expectedProjectDetails, projectService.getProjectDetails(givenCurrentUser, givenProjectId));
    }

    @Test
    void getProjectDetails_shouldThrowEntityNotFoundException_whenProjectDoesNotExists() {
        final var givenCurrentUser = randomUserId();
        final var givenProjectId = randomProjectId();
        doReturn(Optional.empty()).when(findProjectDetailsPort).findProjectDetails(eq(givenProjectId));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.getProjectDetails(givenCurrentUser, givenProjectId)
        );
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void getProjectDetails_shouldThrowInsufficientPrivilegesException_whenUserIsNotProjectMember() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var user = getTestUser();
        final var givenCurrentUser = randomUserId();
        final var givenProjectId = randomProjectId();
        final var owner = ProjectUser.withId(user.getId());
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(owner)
                .members(Set.of(owner))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        final var expectedProjectDetails = new ProjectDetails(project, user, List.of(user));
        doReturn(Optional.of(expectedProjectDetails)).when(findProjectDetailsPort).findProjectDetails(eq(givenProjectId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.getProjectDetails(givenCurrentUser, givenProjectId)
        );
        assertEquals("Current user does not have access to project", exception.getMessage());
    }

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() {
        final var givenUserId = new UserId(10L);
        final var givenCreateProjectDto = getCreateProjectDto();
        doAnswer(invocation -> invocation.getArgument(0)).when(projectRepository).add(any());
        final var created = projectService.createProject(givenUserId, givenCreateProjectDto);
        assertEquals(givenCreateProjectDto.getTitle(), created.getTitle());
        assertEquals(givenCreateProjectDto.getDescription(), created.getDescription());
        assertEquals(givenUserId, created.getOwner().id());
        assertEquals(Set.of(givenUserId), created.getMembers().stream().map(ProjectUser::id).collect(Collectors.toSet()));
    }

    @Test
    void updateProject_shouldReturnUpdatedProject_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenUpdateDto = getUpdateProjectDto();
        final var owner = ProjectUser.withId(givenCurrentUserId);
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(owner)
                .members(Set.of(owner))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        final var expectedProject = Project.builder()
                .id(project.getId())
                .owner(project.getOwner())
                .members(project.getMembers())
                .title(givenUpdateDto.getTitle())
                .description(givenUpdateDto.getDescription())
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        doReturn(expectedProject).when(updateProjectPort)
                .update(eq(givenProjectId), eq(new UpdateProjectCommand(givenUpdateDto.getTitle(), givenUpdateDto.getDescription())));
        final var updated = projectService.updateProject(givenCurrentUserId, givenProjectId, givenUpdateDto);
        assertEquals(expectedProject, updated);
    }

    @Test
    void updateProject_shouldThrowEntityNotFoundException_whenProjectNotFound() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenUpdateDto = getUpdateProjectDto();
        doReturn(Optional.empty()).when(findProjectPort).findById(eq(givenProjectId));
        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.updateProject(givenCurrentUserId, givenProjectId, givenUpdateDto)
        );
        assertEquals("Project not found", exception.getMessage());
        verifyNoInteractions(updateProjectPort);
    }

    @Test
    void updateProject_shouldThrowInsufficientPrivilegesException_whenUserIsNotOwner() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenUpdateDto = getUpdateProjectDto();
        final var owner = ProjectUser.withId(randomUserId());
        final var currentProjectUser = ProjectUser.withId(givenCurrentUserId);
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(owner)
                .members(Set.of(owner, currentProjectUser))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.updateProject(givenCurrentUserId, givenProjectId, givenUpdateDto)
        );
        assertEquals("Operation allowed only to the project owner", exception.getMessage());
        verifyNoInteractions(updateProjectPort);
    }

    @Test
    void addMember_shouldAddMember_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var owner = ProjectUser.withId(givenCurrentUserId);
        final var project = Project.builder()
                .id(randomProjectId())
                .owner(owner)
                .members(Set.of(owner))
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
        final var owner = ProjectUser.withId(givenCurrentUserId);
        final var project = Project.builder()
                .id(randomProjectId())
                .owner(owner)
                .members(Set.of(owner))
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
        final var owner = ProjectUser.withId(randomUserId());
        final var project = Project.builder()
                .id(givenProjectId)
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .owner(owner)
                .members(Set.of(owner))
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.addMember(givenCurrentUserId, givenProjectId, EMAIL)
        );
        assertEquals("Current user does not have access to project", exception.getMessage());
        verifyNoInteractions(addProjectMemberPort);
    }

    private static CreateProjectDto getCreateProjectDto() {
        var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static UpdateProjectDto getUpdateProjectDto() {
        final var updateProjectDto = new UpdateProjectDto();
        updateProjectDto.setTitle("New title");
        updateProjectDto.setDescription("New description");
        return updateProjectDto;
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private List<Project> randomProjectsWithMember(int pageSize, UserId givenUserId) {
        final var owner = ProjectUser.withId(randomUserId());
        final var givenMember = ProjectUser.withId(givenUserId);
        return IntStream.range(0 , pageSize)
                .mapToObj(value -> Project.builder()
                        .id(randomProjectId())
                        .title("Title %d".formatted(value))
                        .description("Description %d".formatted(value))
                        .owner(owner)
                        .members(Set.of(owner, givenMember))
                        .build())
                .toList();
    }
}