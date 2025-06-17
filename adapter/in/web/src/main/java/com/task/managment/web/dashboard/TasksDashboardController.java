package com.task.managment.web.dashboard;

import com.task.management.application.shared.projection.Page;
import com.task.management.application.shared.query.Sort;
import com.task.management.application.dashboard.port.in.TasksDashboardUseCase;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.managment.web.common.BaseController;
import com.task.managment.web.common.dto.PagedResponse;
import com.task.managment.web.dashboard.dto.DashboardTaskPreviewDto;
import com.task.managment.web.dashboard.dto.TasksSummaryDto;
import com.task.managment.web.dashboard.mapper.TasksDashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/tasks")
@RequiredArgsConstructor
public class TasksDashboardController extends BaseController {
    private final TasksDashboardUseCase tasksDashboardUseCase;
    private final TasksDashboardMapper tasksDashboardMapper;

    @GetMapping(
            value = "/assigned/summary",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TasksSummaryDto getAssignedTasksSummary() {
        final var summary = tasksDashboardUseCase.getAssignedToSummary(actor());
        return tasksDashboardMapper.toTasksSummaryDto(summary);
    }

    @GetMapping(
            value = "/assigned",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PagedResponse<DashboardTaskPreviewDto> getAssignedTasks(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final var query = FindAssignedDashboardTasksQuery.builder()
                .assignee(actor())
                .pageSize(size)
                .pageNumber(page)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        return getAssignedDashboardTasksPage(query);
    }

    @GetMapping(
            value = "/assigned/overdue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PagedResponse<DashboardTaskPreviewDto> getOverdueAssignedTasks(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final var query = FindAssignedDashboardTasksQuery.builder()
                .assignee(actor())
                .overdueOnly()
                .pageSize(size)
                .pageNumber(page)
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        return getAssignedDashboardTasksPage(query);
    }

    @GetMapping(
            value = "/owned/summary",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TasksSummaryDto getOwnedTasksSummary() {
        final var summary = tasksDashboardUseCase.getOwnedBySummary(actor());
        return tasksDashboardMapper.toTasksSummaryDto(summary);
    }

    @GetMapping(
            value = "/owned",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PagedResponse<DashboardTaskPreviewDto> getOwnedTasks(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final var query = FindOwnedDashboardTasksQuery.builder()
                .owner(actor())
                .pageSize(size)
                .pageNumber(page)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        return getOwnedDashboardTasksPage(query);
    }

    @GetMapping(
            value = "/owned/overdue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PagedResponse<DashboardTaskPreviewDto> getOverdueOwnedTasks(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final var query = FindOwnedDashboardTasksQuery.builder()
                .owner(actor())
                .overdueOnly()
                .pageSize(size)
                .pageNumber(page)
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        return getOwnedDashboardTasksPage(query);
    }

    private PagedResponse<DashboardTaskPreviewDto> getAssignedDashboardTasksPage(FindAssignedDashboardTasksQuery query) {
        final var result = tasksDashboardUseCase.getAssignedTo(query);
        return mapToPage(result);
    }

    private PagedResponse<DashboardTaskPreviewDto> getOwnedDashboardTasksPage(FindOwnedDashboardTasksQuery query) {
        final var result = tasksDashboardUseCase.getOwnedBy(query);
        return mapToPage(result);
    }

    private PagedResponse<DashboardTaskPreviewDto> mapToPage(Page<DashboardTaskPreview> result) {
        return PagedResponse.<DashboardTaskPreviewDto>builder()
                .currentPage(result.pageNo())
                .pageSize(result.pageSize())
                .total(result.total().longValue())
                .totalPages(result.totalPages().longValue())
                .data(tasksDashboardMapper.toDashboardTaskPreviewDtoList(result.content()))
                .build();
    }
}
