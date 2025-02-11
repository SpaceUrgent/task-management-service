package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.port.in.command.UpdateProjectCommand;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.port.in.AddProjectMemberUseCase;
import com.task.management.application.project.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.project.port.in.GetProjectMembersUseCase;
import com.task.management.application.project.port.in.GetProjectUseCase;
import com.task.management.application.project.port.in.UpdateProjectUseCase;
import com.task.management.application.project.port.in.command.CreateProjectCommand;
import com.task.management.application.project.model.Project;
import com.task.management.application.project.port.in.CreateProjectUseCase;
import com.task.management.application.project.port.out.AddProjectMemberPort;
import com.task.management.application.project.port.out.FindProjectByIdPort;
import com.task.management.application.project.port.out.FindProjectMembersPort;
import com.task.management.application.project.port.out.FindProjectsByMemberPort;
import com.task.management.application.project.port.out.SaveProjectPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.task.management.application.common.Validation.emailRequired;
import static com.task.management.application.common.Validation.parameterRequired;

@Slf4j
@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase,
                                        AddProjectMemberUseCase,
                                        GetAvailableProjectsUseCase,
                                        GetProjectUseCase,
                                        GetProjectMembersUseCase,
                                        UpdateProjectUseCase {
    private final ValidationService validationService;
    private final ProjectUserService projectUserService;
    private final SaveProjectPort saveProjectPort;
    private final AddProjectMemberPort addProjectMemberPort;
    private final FindProjectByIdPort findProjectByIdPort;
    private final FindProjectsByMemberPort findProjectsByMemberPort;
    private final FindProjectMembersPort findProjectMembersPort;

    @Override
    public List<ProjectPreview> getAvailableProjects(ProjectUserId actorId) {
        parameterRequired(actorId, "Actor id");
        return findProjectsByMemberPort.findProjectsByMember(actorId);
    }

    @Override
    public List<ProjectUser> getMembers(ProjectUserId actorId, ProjectId projectId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        checkIsMember(actorId, projectId);
        return findProjectMembersPort.findMembers(projectId);
    }

    @Override
    public Project getProject(ProjectUserId actorId, ProjectId projectId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        checkIsMember(actorId, projectId);
        return findOrThrow(projectId);
    }

    @Override
    public Project createProject(ProjectUserId actorId, CreateProjectCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        validationService.validate(command);
        final var project = Project.builder()
                .title(command.title())
                .description(command.description())
                .owner(projectUserService.getProjectUser(actorId))
                .build();
        return saveProjectPort.save(project);
    }

    @Override
    public Project updateProject(ProjectUserId actorId, UpdateProjectCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        validationService.validate(command);
        final var projectId = command.projectId();
        final var project = findOrThrow(projectId);
        if (!project.isOwner(actorId)) {
            log.debug("User with id {} is not allowed to update project with id {}", actorId, projectId);
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to update project");
        }
        project.setTitle(command.title());
        project.setDescription(command.description());
        return saveProjectPort.save(project);
    }

    @Override
    public void addMember(ProjectUserId actorId, ProjectId projectId, String email) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        emailRequired(email);
        checkIsMember(actorId, projectId);
        final var memberId = projectUserService.getProjectUser(email).id();
        addProjectMemberPort.addMember(projectId, memberId);
    }

    private void checkIsMember(ProjectUserId userId, ProjectId projectId) throws UseCaseException {
        if (!projectUserService.isMember(userId, projectId)) {
            log.debug("User with id {} is not a member of project with id {}", userId, projectId);
            throw new UseCaseException.IllegalAccessException("Current does not have access to project");
        }
    }

    private Project findOrThrow(ProjectId projectId) throws UseCaseException {
        return findProjectByIdPort.find(projectId)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Project with id %d not found".formatted(projectId.value())));
    }
}
