package com.task.management.application.service;

import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberByEmailUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.out.AddProjectMemberPort;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectPort;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.task.management.application.service.ValidationService.projectIdRequired;
import static com.task.management.application.service.ValidationService.userIdRequired;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase,
                                       AddProjectMemberByEmailUseCase {
    private final ValidationService validationService;
    private final UserService userService;
    private final AddProjectPort projectRepository;
    private final AddProjectMemberPort addProjectMemberPort;
    private final FindProjectPort findProjectPort;

    @Override
    public Project createProject(final UserId userId,
                                 final CreateProjectDto createProjectDto) {
        userIdRequired(userId);
        requireNonNull(createProjectDto, "Create project dto is required");
        validationService.validate(createProjectDto);
        final var project = Project.builder()
                .title(createProjectDto.getTitle())
                .description(createProjectDto.getDescription())
                .owner(userId)
                .members(Set.of(userId))
                .build();
        return projectRepository.add(project);
    }

    @Override
    public void addMember(final UserId currentUserId,
                          final ProjectId projectId,
                          final String memberEmail) throws InsufficientPrivilegesException, EntityNotFoundException {
        userIdRequired(currentUserId);
        projectIdRequired(projectId);
        requireNonNull(memberEmail, "Email is required");
        final var project = findOrThrow(projectId);
        if (!project.hasMember(currentUserId)) {
            throw new InsufficientPrivilegesException("Current user is not allowed to add project member");
        }
        final var newMember = userService.getUser(memberEmail);
        addProjectMemberPort.addMember(projectId, newMember.getId());
    }

    private Project findOrThrow(ProjectId projectId) throws EntityNotFoundException {
        return findProjectPort.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }
}
