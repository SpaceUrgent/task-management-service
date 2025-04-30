package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.common.annotation.UseCase;
import com.task.management.application.common.service.UserInfoService;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.project.command.CreateProjectCommand;
import com.task.management.application.project.command.UpdateMemberRoleCommand;
import com.task.management.application.project.command.UpdateProjectCommand;
import com.task.management.application.project.port.in.*;
import com.task.management.application.project.port.out.MemberRepositoryPort;
import com.task.management.application.project.port.out.ProjectRepositoryPort;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.Member;
import com.task.management.domain.project.model.MemberRole;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static com.task.management.domain.common.validation.Validation.*;

@Slf4j
@AppComponent
@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase,
        AddProjectMemberUseCase,
        UpdateMemberRoleUseCase,
                                       GetAvailableProjectsUseCase,
        UpdateProjectUseCase,
        GetProjectDetailsUseCase {
    private final ValidationService validationService;
    private final UserInfoService userService;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final MemberRepositoryPort memberRepositoryPort;

    @UseCase
    @Override
    public List<ProjectPreview> getAvailableProjects(UserId actorId) {
        actorIdRequired(actorId);
        return projectRepositoryPort.findProjectsByMember(actorId);
    }

    @UseCase
    @Override
    public void createProject(UserId actorId, CreateProjectCommand command) {
        actorIdRequired(actorId);
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
    public void updateProject(UserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        validationService.validate(command);
        final var actor = getActingMember(projectId, actorId);
        final var project = findOrThrow(projectId);
        if (!actor.isOwnerOrAdmin()) {
            log.debug("User with id {} is not allowed to update project with id {}", actorId, projectId);
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to update project");
        }
        project.updateTitle(command.title());
        project.updateDescription(command.description());
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void addMember(UserId actorId, ProjectId projectId, Email email) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        emailRequired(email);
        checkIsMember(actorId, projectId);
        final var memberId = userService.getUser(email).id();
        final var member = Member.builder()
                .id(memberId)
                .projectId(projectId)
                .build();
        memberRepositoryPort.save(member);
    }

    @UseCase
    @Override
    public void updateMemberRole(UserId actorId, UpdateMemberRoleCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        validationService.validate(command);
        ProjectId projectId = command.projectId();
        final var actor = getActingMember(projectId, actorId);
        final var targetMember = getProjectMember(projectId, command.memberId());
        if (!actor.isOwner()) raiseOperationNotAllowed();
        final var newRole = command.role();
        targetMember.setRole(newRole);
        memberRepositoryPort.save(targetMember);
        if (MemberRole.OWNER == newRole) {
            actor.setRole(MemberRole.ADMIN);
            memberRepositoryPort.save(actor);
        }
    }

    @UseCase
    @Override
    public ProjectDetails getProjectDetails(UserId actorId, ProjectId projectId) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        checkIsMember(actorId, projectId);
        return findProjectDetailsOrThrow(projectId);
    }

    public void checkIsMember(UserId userId, ProjectId projectId) throws UseCaseException {
        if (!this.isMember(userId, projectId)) {
            log.debug("User with id {} is not a member of project with id {}", userId, projectId);
            throw new UseCaseException.IllegalAccessException("User does not have access to project");
        }
    }

    public boolean isMember(UserId userId, ProjectId projectId) {
        return memberRepositoryPort.find(projectId, userId).isPresent();
    }

    private ProjectDetails findProjectDetailsOrThrow(ProjectId projectId) throws UseCaseException.EntityNotFoundException {
        return projectRepositoryPort.findProjectDetails(projectId)
                .orElseThrow(raiseProjectNotFound(projectId));
    }

    private Project findOrThrow(ProjectId projectId) throws UseCaseException {
        return projectRepositoryPort.find(projectId)
                .orElseThrow(raiseProjectNotFound(projectId));
    }

    private Member getActingMember(ProjectId projectId, UserId memberId) throws UseCaseException.IllegalAccessException {
        return memberRepositoryPort.find(projectId, memberId)
                .orElseThrow(ProjectService::operationNotAllowed);
    }

    private Member getProjectMember(ProjectId projectId, UserId memberId) throws UseCaseException.EntityNotFoundException {
        return memberRepositoryPort.find(projectId, memberId)
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

    private static void projectIdRequired(ProjectId projectId) {
        parameterRequired(projectId, "Project id");
    }
}
