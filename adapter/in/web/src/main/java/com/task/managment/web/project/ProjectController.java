package com.task.managment.web.project;

import com.task.management.application.shared.UseCaseException;
import com.task.management.application.shared.query.Sort;
import com.task.management.application.project.RemoveTaskStatusException;
import com.task.management.application.project.command.*;
import com.task.management.application.project.port.in.*;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import com.task.managment.web.shared.BaseController;
import com.task.managment.web.shared.dto.ErrorResponse;
import com.task.managment.web.shared.dto.ListResponse;
import com.task.managment.web.project.dto.ProjectPreviewDto;
import com.task.managment.web.project.dto.TaskPreviewDto;
import com.task.managment.web.project.dto.UserProjectDetailsDto;
import com.task.managment.web.project.dto.request.*;
import com.task.managment.web.shared.dto.PagedResponse;
import com.task.managment.web.project.mapper.ProjectMapper;
import com.task.managment.web.project.mapper.TaskMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController extends BaseController {
    private final ProjectUseCase projectUseCase;
    private final TaskUseCase taskUseCase;
    private final ProjectMemberUseCase projectMemberUseCase;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    @GetMapping
    public ListResponse<ProjectPreviewDto> getAvailableProjects() {
        final var projectPreviews = projectUseCase.getAvailableProjects(actor());
        return ListResponse.<ProjectPreviewDto>builder()
                .data(projectPreviews.stream().map(projectMapper::toDto).toList())
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createProject(@RequestBody @Valid @NotNull CreateProjectRequest request) throws UseCaseException {
        final var command = CreateProjectCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        projectUseCase.createProject(actor(), command);
    }

    @GetMapping("/{projectId}")
    public UserProjectDetailsDto getProjectDetails(@PathVariable Long projectId) throws UseCaseException {
        final var actor = actor();
        final var projectDetails = projectUseCase.getProjectDetails(actor, new ProjectId(projectId));
        return projectMapper.toDto(actor, projectDetails);
    }

    @PutMapping("/{projectId}")
    public void updateProject(@PathVariable Long projectId,
                              @RequestBody @Valid @NotNull UpdateProjectRequest request) throws UseCaseException {
        final var command = UpdateProjectCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        projectUseCase.updateProject(actor(), new ProjectId(projectId), command);
    }

    @PostMapping("/{projectId}/leave")
    public void leaveProject(@PathVariable Long projectId) throws UseCaseException {
        projectUseCase.leaveProject(actor(), new ProjectId(projectId));
    }

    @PostMapping("/{projectId}/members")
    public void addProjectMember(@PathVariable Long projectId,
                                 @RequestBody @Valid @NotNull AddProjectMemberRequest request) throws UseCaseException {
        projectUseCase.addMember(
                actor(), new ProjectId(projectId), new Email(request.getEmail())
        );
    }

    @PutMapping("/{projectId}/members")
    public void updateMemberRole(@PathVariable Long projectId,
                                 @RequestBody @Valid @NotNull UpdateMemberRoleRequest request) throws UseCaseException {
        final var command = UpdateMemberRoleCommand.builder()
                .projectId(new ProjectId(projectId))
                .memberId(new UserId(request.getMemberId()))
                .role(MemberRole.withRoleName(request.getRole()))
                .build();
        projectMemberUseCase.updateMemberRole(actor(), command);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public void excludeMember(@PathVariable Long projectId,
                              @PathVariable Long memberId) throws UseCaseException {
        projectUseCase.excludeMember(actor(), new ProjectId(projectId), new UserId(memberId));
    }

    @PutMapping("/{projectId}/available-statuses")
    public void addAvailableTaskStatus(@PathVariable Long projectId,
                                       @RequestBody @Valid @NotNull AddTaskStatusRequest request) throws UseCaseException {
        final var command = AddTaskStatusCommand.builder()
                .name(request.getName())
                .position(request.getPosition())
                .build();
        projectUseCase.addTaskStatus(actor(), new ProjectId(projectId), command);
    }

    @DeleteMapping("/{projectId}/available-statuses/{statusName}")
    public void removeAvailableTaskStatus(@PathVariable Long projectId,
                                          @PathVariable String statusName) throws UseCaseException {
        projectUseCase.removeTaskStatus(actor(), new ProjectId(projectId), statusName);
    }

    @GetMapping("/{projectId}/tasks")
    public PagedResponse<TaskPreviewDto> getTasks(@PathVariable Long projectId,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(name = "size", defaultValue = "50") Integer size,
                                                  @RequestParam(name = "assignee", required = false) Set<Long> assignee,
                                                  @RequestParam(name = "unassigned", required = false) Boolean unassigned,
                                                  @RequestParam(name = "status", required = false) Set<String> statusList,
                                                  @RequestParam(name = "sortBy", defaultValue = "createdAt:DESC") List<String> sortBy) throws UseCaseException {
        final var sortList = toSortList(sortBy);
        final var assignees = Optional.ofNullable(assignee)
                .map(assigneeIdSet -> assigneeIdSet.stream().map(UserId::new).collect(Collectors.toSet()))
                .orElse(null);
        final var query = FindTasksQuery.builder()
                .pageNumber(page)
                .pageSize(size)
                .projectId(new ProjectId(projectId))
                .assignees(assignees)
                .includeUnassigned(unassigned)
                .statusIn(statusList)
                .sortBy(sortList)
                .build();
        final var taskPreviewPage = taskUseCase.findTasks(actor(), query);
        final var data = taskPreviewPage.content().stream()
                .map(taskMapper::toDto)
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
                .assigneeId(new UserId(request.getAssigneeId()))
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(TaskPriority.withPriorityName(request.getPriority()))
                .build();
        taskUseCase.createTask(actor(), new ProjectId(projectId), command);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RemoveTaskStatusException.class)
    public ErrorResponse handleRemoveTaskStatusException(RemoveTaskStatusException exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason("Conflict raised during request processing")
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    private static List<Sort> toSortList(List<String> sortBy) {
        return sortBy.stream()
                .map(String::trim)
                .map(value -> value.split(":"))
                .filter(keyValueArray -> keyValueArray.length == 2)
                .map(keyValueArray -> Sort.by(keyValueArray[0], Sort.Direction.valueOf(keyValueArray[1])))
                .toList();
    }
}
