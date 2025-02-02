package com.task.management.application.service;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.service.mapper.ProjectMapper;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.dto.CreateProjectDto;
import com.task.management.application.dto.UpdateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.GetProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.ProjectHasMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.task.management.application.service.Validation.parameterRequired;
import static com.task.management.application.service.Validation.projectIdRequired;
import static com.task.management.application.service.Validation.userIdRequired;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase,
                                       AddProjectMemberUseCase,
                                       GetAvailableProjectsUseCase,
                                       GetProjectDetailsUseCase,
                                       UpdateProjectUseCase {
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final ValidationService validationService;
    private final AddProjectPort addProjectPort;
    private final ProjectHasMemberPort projectHasMemberPort;
    private final AddProjectMemberPort addProjectMemberPort;
    private final FindProjectPort findProjectPort;
    private final FindProjectsByMemberPort findProjectsByMemberPort;
    private final GetProjectDetailsPort getProjectDetailsPort;
    private final UpdateProjectPort updateProjectPort;

    @Override
    public List<ProjectDTO> getAvailableProjects(UserId userId, PageQuery page) {
        parameterRequired(userId, "User id");
        parameterRequired(page, "Page query");
        return findProjectsByMemberPort.findProjectsByMember(userId, page)
                .stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    @Override
    public ProjectDetailsDTO getProjectDetails(UserId currentUser, ProjectId projectId) throws EntityNotFoundException, InsufficientPrivilegesException {
        userIdRequired(currentUser);
        projectIdRequired(projectId);
        checkUserIsMember(currentUser, projectId);
        return getProjectDetailsPort.getProjectDetails(projectId);
    }

    @Override
    public ProjectDTO createProject(final UserId userId,
                                    final CreateProjectDto createProjectDto) {
        userIdRequired(userId);
        requireNonNull(createProjectDto, "Create project dto is required");
        validationService.validate(createProjectDto);
        final var owner = ProjectUser.withId(userId);
        final var project = Project.builder()
                .title(createProjectDto.getTitle())
                .description(createProjectDto.getDescription())
                .owner(owner)
                .build();
        return projectMapper.toDTO(addProjectPort.add(project));
    }

    @Override
    public ProjectDTO updateProject(final UserId currentUser,
                                 final ProjectId projectId,
                                 final UpdateProjectDto updateProjectDto) throws EntityNotFoundException, InsufficientPrivilegesException {
        userIdRequired(currentUser);
        projectIdRequired(projectId);
        requireNonNull(updateProjectDto, "Update project dto is required");
        validationService.validate(updateProjectDto);
        final var project = findOrThrow(projectId);
        checkUserIsOwner(currentUser, project);
        project.setTitle(updateProjectDto.getTitle());
        project.setDescription(updateProjectDto.getDescription());
        return projectMapper.toDTO(updateProjectPort.update(project));
    }

    @Override
    public void addMember(final UserId currentUserId,
                          final ProjectId projectId,
                          final UserId memberId) throws InsufficientPrivilegesException, EntityNotFoundException {
        userIdRequired(currentUserId);
        projectIdRequired(projectId);
        userIdRequired(memberId);
        checkUserIsMember(currentUserId, projectId);
        addProjectMemberPort.addMember(projectId, memberId);
    }

    private void checkUserIsMember(UserId userId, ProjectId projectId) throws InsufficientPrivilegesException {
        if (!projectHasMemberPort.hasMember(projectId, userId)) {
            throw new InsufficientPrivilegesException("Current user does not have access to project");
        }
    }

    private Project findOrThrow(ProjectId projectId) throws EntityNotFoundException {
        return findProjectPort.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private static void checkUserIsOwner(UserId userId, Project project) throws InsufficientPrivilegesException {
        if (!project.isOwner(userId)) {
            throw new InsufficientPrivilegesException("Operation allowed only to the project owner");
        }
    }
}
