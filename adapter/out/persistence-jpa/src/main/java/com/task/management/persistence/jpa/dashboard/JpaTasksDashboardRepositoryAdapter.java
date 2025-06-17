package com.task.management.persistence.jpa.dashboard;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.projection.Page;
import com.task.management.application.dashboard.port.out.TasksDashboardRepository;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dashboard.mapper.DashboardTaskMapper;
import com.task.management.persistence.jpa.query.FindAssignedDashboardTasksQueryAdapter;
import com.task.management.persistence.jpa.query.FindOwnedDashboardTasksQueryAdapter;

@AppComponent
public class JpaTasksDashboardRepositoryAdapter implements TasksDashboardRepository {
    private final TaskEntityDao taskEntityDao;
    private final DashboardTaskMapper dashboardTaskMapper = DashboardTaskMapper.INSTANCE;

    public JpaTasksDashboardRepositoryAdapter(TaskEntityDao taskEntityDao) {
        this.taskEntityDao = taskEntityDao;
    }

    @Override
    public TasksSummary getAssignedToSummary(UserId assignee) {
        final var view = taskEntityDao.getSummaryByAssigneeId(assignee.value());
        return TasksSummary.builder()
                .total(view.total())
                .open(view.open())
                .overdue(view.overdue())
                .closed(view.closed())
                .build();
    }

    @Override
    public TasksSummary getOwnedBySummary(UserId owner) {
        final var view = taskEntityDao.getSummaryByOwnerId(owner.value());
        return TasksSummary.builder()
                .total(view.total())
                .open(view.open())
                .overdue(view.overdue())
                .closed(view.closed())
                .build();
    }

    @Override
    public Page<DashboardTaskPreview> getAssignedTo(FindAssignedDashboardTasksQuery query) {
        final var taskEntityPage = taskEntityDao.findPage(new FindAssignedDashboardTasksQueryAdapter(query));
        return Page.<DashboardTaskPreview>builder()
                .pageNo(query.getPageNumber())
                .pageSize(query.getPageSize())
                .total((int) taskEntityPage.total())
                .totalPages(taskEntityPage.totalPages())
                .content(taskEntityPage.stream().map(dashboardTaskMapper::toDashboardTaskPreview).toList())
                .build();
    }

    @Override
    public Page<DashboardTaskPreview> getOwnedBy(FindOwnedDashboardTasksQuery query) {
        final var taskEntityPage = taskEntityDao.findPage(new FindOwnedDashboardTasksQueryAdapter(query));
        return Page.<DashboardTaskPreview>builder()
                .pageNo(query.getPageNumber())
                .pageSize(query.getPageSize())
                .total((int) taskEntityPage.total())
                .totalPages(taskEntityPage.totalPages())
                .content(taskEntityPage.stream().map(dashboardTaskMapper::toDashboardTaskPreview).toList())
                .build();
    }
}
