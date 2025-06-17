package com.task.management.application.project.service;

import com.task.management.application.shared.UseCaseException;
import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.annotation.UseCase;
import com.task.management.application.shared.service.UserInfoService;
import com.task.management.application.shared.validation.ValidationService;
import com.task.management.application.project.RemoveTaskStatusException;
import com.task.management.application.project.command.AddTaskStatusCommand;
import com.task.management.application.project.command.CreateProjectCommand;
import com.task.management.application.project.command.UpdateMemberRoleCommand;
import com.task.management.application.project.command.UpdateProjectCommand;
import com.task.management.application.project.port.in.*;
import com.task.management.application.project.port.out.MemberRepositoryPort;
import com.task.management.application.project.port.out.ProjectRepositoryPort;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.Member;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static com.task.management.application.project.ProjectConstants.DEFAULT_TASK_STATUSES;
import static com.task.management.domain.shared.validation.Validation.*;

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
    private final TaskRepositoryPort taskRepositoryPort;
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
                .availableTaskStatuses(DEFAULT_TASK_STATUSES)
                .build();
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void updateProject(UserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        validationService.validate(command);
        final var project = getProject(projectId);
        checkActorHasAdminPrivileges(actorId, projectId);
        project.updateTitle(command.title());
        project.updateDescription(command.description());
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void addTaskStatus(UserId actorId, ProjectId projectId, AddTaskStatusCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        validationService.validate(command);
        final var project = getProject(projectId);
        checkActorHasAdminPrivileges(actorId, projectId);
        final var newTaskStatus = TaskStatus.builder()
                .name(command.name())
                .position(command.position())
                .build();
        project.addStatus(newTaskStatus);
        projectRepositoryPort.save(project);
    }

    @UseCase
    @Override
    public void removeTaskStatus(UserId actorId, ProjectId projectId, String statusName) throws UseCaseException {
        actorIdRequired(actorId);
        projectIdRequired(projectId);
        notBlank(statusName, "Status name");
        final var project = getProject(projectId);
        checkActorHasAdminPrivileges(actorId, projectId);
        if (taskRepositoryPort.projectTaskWithStatusExists(projectId, statusName)) {
            throw new RemoveTaskStatusException("Task status '%s' can not be removed. Project has task/s with indicated status".formatted(statusName));
        }
        project.removeStatus(statusName);
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
        return getProjectDetails(projectId);
    }

    public boolean isMember(UserId userId, ProjectId projectId) {
        return memberRepositoryPort.find(projectId, userId).isPresent();
    }

    public void checkIsMember(UserId userId, ProjectId projectId) throws UseCaseException {
        if (!this.isMember(userId, projectId)) {
            log.debug("User with id {} is not a member of project with id {}", userId, projectId);
            throw new UseCaseException.IllegalAccessException("User does not have access to project");
        }
    }

    public TaskStatus getInitialTaskStatus(ProjectId projectId) {
        return getAvailableTaskStatuses(projectId).stream()
                .min(Comparator.comparing(TaskStatus::position))
                .orElseThrow(() -> new IllegalStateException("Project does not have available status"));
    }

    public List<TaskStatus> getAvailableTaskStatuses(ProjectId projectId) {
        return projectRepositoryPort.findAvailableTaskStatuses(projectId);
    }

    private Project getProject(ProjectId projectId) throws UseCaseException {
        return projectRepositoryPort.find(projectId)
                .orElseThrow(raiseProjectNotFound(projectId));
    }

    private void checkActorHasAdminPrivileges(UserId actorId, ProjectId projectId) throws UseCaseException.IllegalAccessException {
        final var actor = getActingMember(projectId, actorId);
        if (!actor.isOwnerOrAdmin()) {
            log.debug("User with id {} is not allowed to update project with id {}", actorId, projectId);
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to update project");
        }
    }

    private ProjectDetails getProjectDetails(ProjectId projectId) throws UseCaseException.EntityNotFoundException {
        return projectRepositoryPort.findProjectDetails(projectId)
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
