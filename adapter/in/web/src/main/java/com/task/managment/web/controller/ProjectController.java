package com.task.managment.web.controller;

import com.task.management.application.common.PageQuery;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberByEmailUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.in.dto.UpdateProjectDto;
import com.task.managment.web.dto.EmailDto;
import com.task.managment.web.dto.PageDto;
import com.task.managment.web.dto.ProjectDetailsDto;
import com.task.managment.web.dto.ProjectDto;
import com.task.managment.web.mapper.WebProjectMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectUseCase createProjectUseCase;
    private final GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    private final GetProjectDetailsUseCase getProjectDetailsUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final AddProjectMemberByEmailUseCase addProjectMemberByEmailUseCase;
    private final WebProjectMapper projectMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid @NotNull
                                    CreateProjectDto createProjectDto) {
        final var project = createProjectUseCase.createProject(currentUser().getId(), createProjectDto);
        return projectMapper.toDto(project);
    }

    @GetMapping
    public PageDto<ProjectDto> getAvailableProjects(@RequestParam(name = "pageNumber", defaultValue = "1")
                                                    Integer pageNumber,
                                                    @RequestParam(name = "pageSize", defaultValue = "20")
                                                    Integer pageSize) {
        final var currentUserId = currentUser().getId();
        final var projects = getAvailableProjectsUseCase.getAvailableProjects(
                currentUserId, new PageQuery(pageNumber, pageSize)
        );
        return PageDto.<ProjectDto>builder()
                .currentPage(pageNumber)
                .pageSize(pageSize)
                .data(projectMapper.toDtoList(projects))
                .build();
    }

    @GetMapping("/{projectId}")
    public ProjectDetailsDto getProjectDetails(@PathVariable Long projectId) throws InsufficientPrivilegesException, EntityNotFoundException {
        final var currentUserId = currentUserId();
        final var projectDetails = getProjectDetailsUseCase.getProjectDetails(currentUserId, new ProjectId(projectId));
        return projectMapper.toDto(projectDetails);
    }

    @PatchMapping("/{projectId}")
    public ProjectDto updateProjectInfo(@PathVariable
                                        Long projectId,
                                        @RequestBody @Valid @NotNull
                                        UpdateProjectDto updateProjectDto) throws InsufficientPrivilegesException, EntityNotFoundException {
        final var updated = updateProjectUseCase.updateProject(
                currentUserId(),
                new ProjectId(projectId),
                updateProjectDto
        );
        return projectMapper.toDto(updated);
    }

    @PutMapping("/{projectId}/members")
    public void addProjectMember(@PathVariable
                                 Long projectId,
                                 @RequestBody @Valid @NotNull
                                 EmailDto emailDto) throws InsufficientPrivilegesException, EntityNotFoundException {
        addProjectMemberByEmailUseCase.addMember(
                currentUserId(), new ProjectId(projectId), emailDto.getEmail()
        );
    }

    private UserId currentUserId() {
        return currentUser().getId();
    }


    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
