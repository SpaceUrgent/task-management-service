package com.task.management.application.service;

import com.task.management.application.common.PageQuery;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.in.dto.UpdateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        final var expectedProjects = randomProjects(givenPage.getPageSize());
        expectedProjects.forEach(project -> project.setMembers(Set.of(givenUserId)));
        doReturn(expectedProjects).when(findProjectsByMemberPort).findProjectsByMember(eq(givenUserId), eq(givenPage));
        assertEquals(expectedProjects, projectService.getAvailableProjects(givenUserId, givenPage));
    }

    @Test
    void getProjectDetails_shouldReturnProjectDetails_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var user = getTestUser();
        final var givenCurrentUser = user.getId();
        final var givenProjectId = randomProjectId();
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(givenCurrentUser)
                .members(Set.of(givenCurrentUser))
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
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(user.getId())
                .members(Set.of(user.getId()))
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
        assertEquals(givenUserId, created.getOwner());
        assertEquals(Set.of(givenUserId), created.getMembers());
    }

    @Test
    void updateProject_shouldReturnUpdatedProject_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenUpdateDto = getUpdateProjectDto();
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(givenCurrentUserId)
                .members(Set.of(givenCurrentUserId))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        doAnswer(invocation -> invocation.getArgument(0)).when(updateProjectPort).update(any());
        final var updated = projectService.updateProject(givenCurrentUserId, givenProjectId, givenUpdateDto);
        assertEquals(project.getId(), updated.getId());
        assertEquals(givenUpdateDto.getTitle(), project.getTitle());
        assertEquals(givenUpdateDto.getDescription(), project.getDescription());
        assertEquals(project.getOwner(), updated.getOwner());
        assertEquals(project.getMembers(), updated.getMembers());
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
        final var owner = randomUserId();
        final var project = Project.builder()
                .id(givenProjectId)
                .owner(owner)
                .members(Set.of(owner, givenCurrentUserId))
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

    private List<Project> randomProjects(int pageSize) {
        return IntStream.range(0 , pageSize)
                .mapToObj(value -> Project.builder()
                        .id(randomProjectId())
                        .title("Title %d".formatted(value))
                        .description("Description %d".formatted(value))
                        .owner(randomUserId())
                        .members(Set.of(randomUserId()))
                        .build())
                .toList();
    }
}