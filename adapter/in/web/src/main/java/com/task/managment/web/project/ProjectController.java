package com.task.managment.web.project;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.application.query.Sort;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.in.AddProjectMemberUseCase;
import com.task.management.domain.project.port.in.CreateProjectUseCase;
import com.task.management.domain.project.port.in.CreateTaskUseCase;
import com.task.management.domain.project.port.in.FindTasksUseCase;
import com.task.management.domain.project.port.in.GetAvailableProjectsUseCase;
import com.task.management.domain.project.port.in.GetProjectDetailsUseCase;
import com.task.management.domain.project.port.in.GetProjectMembersUseCase;
import com.task.management.domain.project.port.in.UpdateProjectUseCase;
import com.task.management.domain.project.application.command.CreateProjectCommand;
import com.task.management.domain.project.application.command.CreateTaskCommand;
import com.task.management.domain.project.application.command.UpdateProjectCommand;
import com.task.management.domain.project.application.query.FindTasksQuery;
import com.task.managment.web.ListResponse;
import com.task.managment.web.project.dto.ProjectDetailsDto;
import com.task.managment.web.project.dto.ProjectPreviewDto;
import com.task.managment.web.project.dto.ProjectUserDto;
import com.task.managment.web.project.dto.TaskPreviewDto;
import com.task.managment.web.project.dto.request.CreateTaskRequest;
import com.task.managment.web.project.dto.request.UpdateProjectRequest;
import com.task.managment.web.project.dto.request.AddProjectMemberRequest;
import com.task.managment.web.project.dto.request.CreateProjectRequest;
import com.task.managment.web.PagedResponse;
import com.task.managment.web.project.mapper.ProjectMapper;
import com.task.managment.web.project.mapper.ProjectUserMapper;
import com.task.managment.web.project.mapper.TaskMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController extends BaseController {
    private final GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    private final GetProjectMembersUseCase getProjectMembersUseCase;
    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectDetailsUseCase getProjectDetailsUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final AddProjectMemberUseCase addProjectMemberUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final FindTasksUseCase findTasksUseCase;
    private final ProjectMapper projectMapper;
    private final ProjectUserMapper projectUserDtoMapper;
    private final TaskMapper taskMapper;

    @GetMapping
    public ListResponse<ProjectPreviewDto> getAvailableProjects() {
        final var projectPreviews = getAvailableProjectsUseCase.getAvailableProjects(actorId());
        return ListResponse.<ProjectPreviewDto>builder()
                .data(projectPreviews.stream().map(projectMapper::toDto).toList())
                .build();
    }

    @GetMapping("/{projectId}/members")
    public ListResponse<ProjectUserDto> getProjectMembers(@PathVariable Long projectId) throws UseCaseException {
        final var data = getProjectMembersUseCase.getMembers(actorId(), new ProjectId(projectId)).stream()
                .map(projectUserDtoMapper::toDto)
                .toList();
        return ListResponse.<ProjectUserDto>builder()
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
        createProjectUseCase.createProject(actorId(), command);
    }

    @GetMapping("/{projectId}")
    public ProjectDetailsDto getProjectDetails(@PathVariable Long projectId) throws UseCaseException {
        final var projectDetails = getProjectDetailsUseCase.getProjectDetails(actorId(), new ProjectId(projectId));
        return projectMapper.toDto(projectDetails);
    }

    @PutMapping("/{projectId}")
    public void updateProject(@PathVariable Long projectId,
                              @RequestBody @Valid @NotNull UpdateProjectRequest request) throws UseCaseException {
        final var command = UpdateProjectCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        updateProjectUseCase.updateProject(actorId(), new ProjectId(projectId), command);
    }

    @PostMapping("/{projectId}/members")
    public void addProjectMember(@PathVariable Long projectId,
                                 @RequestBody @Valid @NotNull AddProjectMemberRequest request) throws UseCaseException {
        addProjectMemberUseCase.addMember(
                actorId(), new ProjectId(projectId), new Email(request.getEmail())
        );
    }

    @GetMapping("/{projectId}/tasks")
    public PagedResponse<TaskPreviewDto> getTasks(@PathVariable Long projectId,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(name = "size", defaultValue = "50") Integer size,
                                                  @RequestParam(name = "assigneeId", required = false) Long assigneeId,
                                                  @RequestParam(name = "status", required = false) Set<String> statusList,
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
        final var taskPreviewPage = findTasksUseCase.findTasks(actorId(), query);
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
                .assigneeId(new ProjectUserId(request.getAssigneeId()))
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        createTaskUseCase.createTask(actorId(), new ProjectId(projectId), command);
    }

    private static List<Sort> toSortList(List<String> sortBy) {
        return sortBy.stream()
                .map(String::trim)
                .map(value -> value.split(":"))
                .filter(keyValueArray -> keyValueArray.length == 2)
                .map(keyValueArray -> Sort.by(keyValueArray[0], Sort.Direction.valueOf(keyValueArray[1])))
                .toList();
    }

    private Set<TaskStatus> toTaskStatusSet(Set<String> statusList) {
        if (statusList == null) return null;
        Set<String> taskStatusNames = TaskStatus.all().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return statusList.stream()
                .filter(taskStatusNames::contains)
                .map(TaskStatus::valueOf)
                .collect(Collectors.toSet());
    }
}
