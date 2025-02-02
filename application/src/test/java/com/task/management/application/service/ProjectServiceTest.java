package com.task.management.application.service;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.UserId;
import com.task.management.application.dto.CreateProjectDto;
import com.task.management.application.dto.UpdateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.GetProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.ProjectHasMemberPort;
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

import static com.task.management.application.service.TestUtils.assertMatches;
import static com.task.management.application.service.TestUtils.randomLong;
import static com.task.management.application.service.TestUtils.randomProject;
import static com.task.management.application.service.TestUtils.randomProjectDetailsDTO;
import static com.task.management.application.service.TestUtils.randomProjects;
import static com.task.management.application.service.TestUtils.randomUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
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
    private FindProjectPort findProjectPort;
    @Mock
    private ProjectHasMemberPort projectHasMemberPort;
    @Mock
    private AddProjectMemberPort addProjectMemberPort;
    @Mock
    private FindProjectsByMemberPort findProjectsByMemberPort;
    @Mock
    private GetProjectDetailsPort findProjectDetailsPort;
    @Mock
    private UpdateProjectPort updateProjectPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void getAvailableProjects_shouldReturnProjectList() {
        final var givenPage = new PageQuery(1, 10);
        final var givenUserId = randomUserId();
        final var availableProjects = randomProjects(givenPage.getPageSize());
        doReturn(availableProjects).when(findProjectsByMemberPort).findProjectsByMember(eq(givenUserId), eq(givenPage));
        final var availableProjectDTOs = projectService.getAvailableProjects(givenUserId, givenPage);
        assertEquals(availableProjects.size(), availableProjectDTOs.size());
        for (int i = 0; i < availableProjects.size(); i++) {
            assertMatches(availableProjects.get(i), availableProjectDTOs.get(i));
        }
    }

    @Test
    void getProjectDetails_shouldReturnProjectDetails_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var expectedProjectDetails = randomProjectDetailsDTO();
        final var givenUserId = new UserId(expectedProjectDetails.owner().id());
        final var givenProjectId = new ProjectId(expectedProjectDetails.id());
        doReturn(true).when(projectHasMemberPort).hasMember(eq(givenProjectId), eq(givenUserId));
        doReturn(expectedProjectDetails).when(findProjectDetailsPort).getProjectDetails(eq(givenProjectId));
        assertEquals(expectedProjectDetails, projectService.getProjectDetails(givenUserId, givenProjectId));
    }

    @Test
    void getProjectDetails_shouldThrowInsufficientPrivilegesException_whenUserIsNotProjectMember() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        doReturn(false).when(projectHasMemberPort).hasMember(eq(givenProjectId), eq(givenUserId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.getProjectDetails(givenUserId, givenProjectId)
        );
        assertEquals("Current user does not have access to project", exception.getMessage());
    }

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() {
        final var givenUserId = new UserId(10L);
        final var givenCreateProjectDto = getCreateProjectDto();
        final var expectedProjectIdValue = randomLong();
        final var expectedProjectDTO = ProjectDTO.builder()
                .id(expectedProjectIdValue)
                .title(givenCreateProjectDto.getTitle())
                .description(givenCreateProjectDto.getDescription())
                .owner(ProjectUserDTO.builder()
                        .id(givenUserId.value())
                        .build())
                .build();
        doAnswer(invocation -> {
            var argument = (Project) invocation.getArgument(0);
            return Project.builder()
                    .id(new ProjectId(expectedProjectIdValue))
                    .title(argument.getTitle())
                    .description(argument.getDescription())
                    .owner(argument.getOwner())
                    .build();
        }).when(projectRepository).add(any());
        final var projectDTO = projectService.createProject(givenUserId, givenCreateProjectDto);
        assertEquals(expectedProjectDTO, projectDTO);
    }

    @Test
    void updateProject_shouldReturnUpdatedProject_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var project = randomProject();
        final var givenCurrentUserId = project.getOwner().id();
        final var givenProjectId = project.getId();
        final var givenUpdateDto = getUpdateProjectDto();
        final var expectedProject = Project.builder()
                .id(project.getId())
                .owner(project.getOwner())
                .title(givenUpdateDto.getTitle())
                .description(givenUpdateDto.getDescription())
                .build();
        doReturn(Optional.of(project)).when(findProjectPort).findById(eq(givenProjectId));
        doReturn(expectedProject).when(updateProjectPort).update(eq(expectedProject));
        final var updated = projectService.updateProject(givenCurrentUserId, givenProjectId, givenUpdateDto);
        assertMatches(expectedProject, updated);
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
        final var project = randomProject();
        final var givenCurrentUserId = randomUserId();
        final var givenProjectId = project.getId();
        final var givenUpdateDto = getUpdateProjectDto();
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
        final var givenProjectId = randomProjectId();
        final var givenMemberId = randomUserId();
        doReturn(true).when(projectHasMemberPort).hasMember(eq(givenProjectId), eq(givenCurrentUserId));
        projectService.addMember(givenCurrentUserId, givenProjectId, givenMemberId);
        verify(addProjectMemberPort).addMember(eq(givenProjectId), eq(givenMemberId));
    }

//    @Test
//    void addMember_shouldThrowEntityNotFoundException_whenProjectDoesNotExists() {
//        final var givenCurrentUserId = randomUserId();
//        final var givenMemberId = randomUserId();
//        final var givenProjectId = randomProjectId();
//        doReturn(Optional.empty()).when(findProjectPort).findById(eq(givenProjectId));
//        final var exception = assertThrows(
//                EntityNotFoundException.class,
//                () -> projectService.addMember(givenCurrentUserId, givenProjectId, givenMemberId)
//        );
//        assertEquals("Project not found", exception.getMessage());
//        verifyNoInteractions(addProjectMemberPort);
//    }

    @Test
    void addMember_shouldThrowInsufficientPrivilegesException_whenCurrentUserIsNotProjectMember() {
        final var givenCurrentUserId = randomUserId();
        final var givenMemberId = randomUserId();
        final var givenProjectId = randomProjectId();

        doReturn(false).when(projectHasMemberPort).hasMember(eq(givenProjectId), eq(givenCurrentUserId));
        final var exception = assertThrows(
                InsufficientPrivilegesException.class,
                () -> projectService.addMember(givenCurrentUserId, givenProjectId, givenMemberId)
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

    private List<ProjectDTO> randomProjectsDTOs(int pageSize) {
        return IntStream.range(0 , pageSize)
                .mapToObj(value -> ProjectDTO.builder()
                        .id(randomLong())
                        .title("Title %d".formatted(value))
                        .description("Description %d".formatted(value))
                        .build())
                .toList();
    }
}