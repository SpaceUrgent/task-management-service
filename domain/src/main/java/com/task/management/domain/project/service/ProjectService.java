package com.task.management.domain.project.service;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.annotation.UseCase;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.port.in.*;
import com.task.management.domain.project.port.in.command.CreateProjectCommand;
import com.task.management.domain.project.port.in.command.UpdateMemberRoleCommand;
import com.task.management.domain.project.port.in.command.UpdateProjectCommand;
import com.task.management.domain.project.port.out.ProjectMemberRepositoryPort;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Slf4j
@AppComponent
@RequiredArgsConstructor
public class ProjectService implements  CreateProjectUseCase,
                                        AddProjectMemberUseCase,
                                        UpdateMemberRoleUseCase,
                                        GetAvailableProjectsUseCase,
                                        GetProjectMembersUseCase,
                                        UpdateProjectUseCase,
                                        GetProjectDetailsUseCase {
    private final ValidationService validationService;
    private final ProjectUserService projectUserService;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectMemberRepositoryPort projectMemberRepositoryPort;

    @UseCase
    @Override
    public List<ProjectPreview> getAvailableProjects(ProjectUserId actorId) {
        parameterRequired(actorId, "Actor id");
        return projectRepositoryPort.findProjectsByMember(actorId);
    }

    @UseCase
    @Override
    public List<ProjectUser> getMembers(ProjectUserId actorId, ProjectId projectId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        checkIsMember(actorId, projectId);
        return projectRepositoryPort.findMembers(projectId);
    }

    @UseCase
    @Override
    public void createProject(ProjectUserId actorId, CreateProjectCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        validationService.validate(command);
        final var project = Project.builder()
                .createdAt(Instant.now())
                .title(command.title())
                .description(command.description())
                .ownerId(actorId)
                .build();
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void updateProject(ProjectUserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        validationService.validate(command);
        final var project = findOrThrow(projectId);
        if (!project.isOwnedBy(actorId)) {
            log.debug("User with id {} is not allowed to update project with id {}", actorId, projectId);
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to update project");
        }
        project.updateTitle(command.title());
        project.updateDescription(command.description());
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void addMember(ProjectUserId actorId, ProjectId projectId, Email email) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        emailRequired(email);
        checkIsMember(actorId, projectId);
        final var memberId = projectUserService.getProjectUser(email).id();
        projectRepositoryPort.addMember(projectId, memberId);
    }

    @UseCase
    @Override
    public void updateMemberRole(MemberId actorId, UpdateMemberRoleCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        validationService.validate(command);
        ProjectId projectId = command.projectId();
        final var actor = getActingMember(projectId, new MemberId(actorId.value()));
        final var targetMember = getProjectMember(projectId, new MemberId(command.memberId().value()));
        if (!actor.isOwner()) raiseOperationNotAllowed();
        final var newRole = command.role();
        targetMember.setRole(newRole);
        projectMemberRepositoryPort.update(targetMember);
        if (MemberRole.OWNER == newRole) {
            actor.setRole(MemberRole.ADMIN);
            projectMemberRepositoryPort.update(actor);
        }
    }

    @UseCase
    @Override
    public ProjectDetails getProjectDetails(ProjectUserId actorId, ProjectId projectId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        checkIsMember(actorId, projectId);
        return findProjectDetailsOrThrow(projectId);
    }

    public void checkIsMember(ProjectUserId userId, ProjectId projectId) throws UseCaseException {
        if (!this.isMember(userId, projectId)) {
            log.debug("User with id {} is not a member of project with id {}", userId, projectId);
            throw new UseCaseException.IllegalAccessException("Current does not have access to project");
        }
    }

    public boolean isMember(ProjectUserId userId, ProjectId projectId) {
        return projectRepositoryPort.isMember(userId, projectId);
    }

    private ProjectDetails findProjectDetailsOrThrow(ProjectId projectId) throws UseCaseException.EntityNotFoundException {
        return projectRepositoryPort.findProjectDetails(projectId)
                .orElseThrow(raiseProjectNotFound(projectId));
    }

    private Project findOrThrow(ProjectId projectId) throws UseCaseException {
        return projectRepositoryPort.find(projectId)
                .orElseThrow(raiseProjectNotFound(projectId));
    }

    private Member getActingMember(ProjectId projectId, MemberId memberId) throws UseCaseException.IllegalAccessException {
        return projectMemberRepositoryPort.findMember(projectId, memberId)
                .orElseThrow(ProjectService::operationNotAllowed);
    }

    private Member getProjectMember(ProjectId projectId, MemberId memberId) throws UseCaseException.EntityNotFoundException {
        return projectMemberRepositoryPort.findMember(projectId, memberId)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Updated member not found"));
    }

    private static Supplier<UseCaseException.EntityNotFoundException> raiseProjectNotFound(ProjectId projectId) {
        return () -> new UseCaseException.EntityNotFoundException("Project with id %d not found".formatted(projectId.value()));
    }

    private static void raiseOperationNotAllowed() throws UseCaseException.IllegalAccessException {
        throw operationNotAllowed();
    }

    private static UseCaseException.IllegalAccessException operationNotAllowed() {
        return new UseCaseException.IllegalAccessException("Operation not allowed");
    }
}
