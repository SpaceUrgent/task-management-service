package com.task.management.application.service;

import com.task.management.application.model.Project;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.out.AddProjectPort;
import lombok.RequiredArgsConstructor;

import static com.task.management.application.service.ValidationService.userIdRequired;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ProjectService implements CreateProjectUseCase {
    private final ValidationService validationService;
    private final AddProjectPort projectRepository;

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
                .build();
        return projectRepository.add(project);
    }
}
