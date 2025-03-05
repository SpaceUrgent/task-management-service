package com.task.managment.web.controller;

import com.task.management.application.common.Sort;
import com.task.management.application.common.UseCaseException;
import com.task.management.application.port.in.command.UpdateProjectCommand;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.AddProjectMemberUseCase;
import com.task.management.application.project.port.in.CreateProjectUseCase;
import com.task.management.application.project.port.in.CreateTaskUseCase;
import com.task.management.application.project.port.in.FindTasksUseCase;
import com.task.management.application.project.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.project.port.in.GetProjectMembersUseCase;
import com.task.management.application.project.port.in.UpdateProjectUseCase;
import com.task.management.application.project.port.in.command.CreateProjectCommand;
import com.task.management.application.project.port.in.command.CreateTaskCommand;
import com.task.management.application.project.port.in.query.FindTasksQuery;
import com.task.managment.web.dto.TaskPreviewDto;
import com.task.managment.web.dto.request.CreateTaskRequest;
import com.task.managment.web.dto.request.UpdateProjectRequest;
import com.task.managment.web.dto.response.AvailableProjectsResponse;
import com.task.managment.web.dto.request.AddProjectMemberRequest;
import com.task.managment.web.dto.request.CreateProjectRequest;
import com.task.managment.web.dto.response.PagedResponse;
import com.task.managment.web.dto.response.ProjectMembersResponse;
import com.task.managment.web.mapper.ProjectPreviewDtoMapper;
import com.task.managment.web.mapper.ProjectUserDtoMapper;
import com.task.managment.web.mapper.TaskPreviewDtoMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    private final GetProjectMembersUseCase getProjectMembersUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final AddProjectMemberUseCase addProjectMemberUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final FindTasksUseCase findTasksUseCase;
    private final ProjectPreviewDtoMapper projectPreviewDtoMapper;
    private final ProjectUserDtoMapper projectUserDtoMapper;
    private final TaskPreviewDtoMapper taskPreviewDtoMapper;

    @GetMapping
    public AvailableProjectsResponse getAvailableProjects() {
        final var projectPreviews = getAvailableProjectsUseCase.getAvailableProjects(currentUserId());
        return AvailableProjectsResponse.builder()
                .data(projectPreviews.stream().map(projectPreviewDtoMapper::toDto).toList())
                .build();
    }

    @GetMapping("/{projectId}/members")
    public ProjectMembersResponse getProjectMembers(@PathVariable Long projectId) throws UseCaseException {
        final var data = getProjectMembersUseCase.getMembers(currentUserId(), new ProjectId(projectId)).stream()
                .map(projectUserDtoMapper::toDto)
                .toList();
        return ProjectMembersResponse.builder()
                .data(data)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createProject(@RequestBody @Valid @NotNull CreateProjectRequest request) throws UseCaseException {
        final var command = CreateProjectCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        createProjectUseCase.createProject(currentUserId(), command);
    }

    @PutMapping("/{projectId}")
    public void updateProject(@PathVariable Long projectId,
                              @RequestBody @Valid @NotNull UpdateProjectRequest request) throws UseCaseException {
        final var command = UpdateProjectCommand.builder()
                .projectId(new ProjectId(projectId))
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        updateProjectUseCase.updateProject(currentUserId(), command);
    }

    @PostMapping("/{projectId}/members/{memberId}")
    public void addProjectMember(@PathVariable Long projectId,
                                 @RequestBody @Valid @NotNull AddProjectMemberRequest request) throws UseCaseException {
        addProjectMemberUseCase.addMember(
                currentUserId(), new ProjectId(projectId), request.getEmail()
        );
    }

    @GetMapping("/{projectId}/tasks")
    public PagedResponse<TaskPreviewDto> getTasks(@PathVariable Long projectId,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(name = "size", defaultValue = "50") Integer size,
                                                  @RequestParam(name = "assigneeId", required = false) Long assigneeId,
                                                  @RequestParam(name = "status", required = false) List<String> statusList,
                                                  @RequestParam(name = "sortBy", defaultValue = "createdAt:DESC") List<String> sortBy) throws UseCaseException {
        final var statusIn = toTaskStatusSet(statusList);
        final var sortList = toSortList(sortBy);
        final var query = FindTasksQuery.builder()
                .pageNumber(page)
                .pageSize(size)
                .projectId(new ProjectId(projectId))
                .assigneeId(Optional.ofNullable(assigneeId).map(ProjectUserId::new).orElse(null))
                .statusIn(statusIn)
                .sortBy(sortList)
                .build();
        final var taskPreviewPage = findTasksUseCase.findTasks(currentUserId(), query);
        final var data = taskPreviewPage.content().stream()
                .map(taskPreviewDtoMapper::toDto)
                .toList();
        return PagedResponse.<TaskPreviewDto>builder()
                .currentPage(taskPreviewPage.pageNo())
                .pageSize(taskPreviewPage.pageSize())
                .total(taskPreviewPage.total().longValue())
                .totalPages(taskPreviewPage.totalPages().longValue())
                .data(data)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{projectId}/tasks")
    public void createTask(@PathVariable Long projectId,
                           @RequestBody @Valid @NonNull CreateTaskRequest request) throws UseCaseException {
        final var command = CreateTaskCommand.builder()
                .projectId(new ProjectId(projectId))
                .assigneeId(new ProjectUserId(request.getAssigneeId()))
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        createTaskUseCase.createTask(currentUserId(), command);
    }

    private ProjectUserId currentUserId() {
        return currentUser().getProjectUserId();
    }


    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }

    private static List<Sort> toSortList(List<String> sortBy) {
        return sortBy.stream()
                .map(String::trim)
                .map(value -> value.split(":"))
                .filter(keyValueArray -> keyValueArray.length == 2)
                .map(keyValueArray -> Sort.by(keyValueArray[0], Sort.Direction.valueOf(keyValueArray[1])))
                .toList();
    }

    private Set<TaskStatus> toTaskStatusSet(List<String> statusList) {
        return Optional.ofNullable(statusList)
                .orElse(new ArrayList<>()).stream()
                .map(TaskStatus::new)
                .collect(Collectors.toSet());
    }
}
