package com.task.management.application.service;

import com.task.management.application.common.PageQuery;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberByEmailUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.in.dto.UpdateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectDetailsPort;
import com.task.management.application.port.out.FindProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.application.port.out.UpdateProjectPort;
import com.task.management.application.port.out.UpdateProjectPort.UpdateProjectCommand;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.task.management.application.service.ValidationService.projectIdRequired;
import static com.task.management.application.service.ValidationService.userIdRequired;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase,
                                       AddProjectMemberByEmailUseCase,
                                       GetAvailableProjectsUseCase,
                                       GetProjectDetailsUseCase,
                                       UpdateProjectUseCase {
    private final ValidationService validationService;
    private final UserService userService;
    private final AddProjectPort addProjectPort;
    private final AddProjectMemberPort addProjectMemberPort;
    private final FindProjectPort findProjectPort;
    private final FindProjectsByMemberPort findProjectsByMemberPort;
    private final FindProjectDetailsPort findProjectDetailsPort;
    private final UpdateProjectPort updateProjectPort;

    @Override
    public List<Project> getAvailableProjects(UserId userId, PageQuery page) {
        userIdRequired(userId);
        requireNonNull(page, "Page is required");
        return findProjectsByMemberPort.findProjectsByMember(userId, page);
    }

    @Deprecated
    @Override
    public ProjectDetails getProjectDetails(UserId currentUser, ProjectId projectId) throws EntityNotFoundException, InsufficientPrivilegesException {
        userIdRequired(currentUser);
        projectIdRequired(projectId);
        final var projectDetails = findProjectDetailsOrThrow(projectId);
        checkUserIsMember(currentUser, projectDetails.project());
        return projectDetails;
    }

    @Override
    public Project createProject(final UserId userId,
                                 final CreateProjectDto createProjectDto) {
        userIdRequired(userId);
        requireNonNull(createProjectDto, "Create project dto is required");
        validationService.validate(createProjectDto);
        final var owner = ProjectUser.withId(userId);
        final var project = Project.builder()
                .title(createProjectDto.getTitle())
                .description(createProjectDto.getDescription())
                .owner(owner)
                .members(Set.of(owner))
                .build();
        return addProjectPort.add(project);
    }

    @Override
    public Project updateProject(final UserId currentUser,
                                 final ProjectId projectId,
                                 final UpdateProjectDto updateProjectDto) throws EntityNotFoundException, InsufficientPrivilegesException {
        userIdRequired(currentUser);
        projectIdRequired(projectId);
        requireNonNull(updateProjectDto, "Update project dto is required");
        validationService.validate(updateProjectDto);
        final var project = findOrThrow(projectId);
        checkUserIsOwner(currentUser, project);
        return updateProjectPort.update(projectId, new UpdateProjectCommand(updateProjectDto.getTitle(), updateProjectDto.getDescription()));
    }

    @Override
    public void addMember(final UserId currentUserId,
                          final ProjectId projectId,
                          final String memberEmail) throws InsufficientPrivilegesException, EntityNotFoundException {
        userIdRequired(currentUserId);
        projectIdRequired(projectId);
        requireNonNull(memberEmail, "Email is required");
        final var project = findOrThrow(projectId);
        checkUserIsMember(currentUserId, project);
        final var newMember = userService.getUser(memberEmail);
        addProjectMemberPort.addMember(projectId, newMember.getId());
    }

    private Project findOrThrow(ProjectId projectId) throws EntityNotFoundException {
        return findProjectPort.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private ProjectDetails findProjectDetailsOrThrow(ProjectId projectId) throws EntityNotFoundException {
        return findProjectDetailsPort.findProjectDetails(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private static void checkUserIsOwner(UserId userId, Project project) throws InsufficientPrivilegesException {
        if (!project.isOwner(userId)) {
            throw new InsufficientPrivilegesException("Operation allowed only to the project owner");
        }
    }

    private static void checkUserIsMember(UserId currentUserId, Project project) throws InsufficientPrivilegesException {
        if (!project.hasMember(currentUserId)) {
            throw new InsufficientPrivilegesException("Current user does not have access to project");
        }
    }
}
