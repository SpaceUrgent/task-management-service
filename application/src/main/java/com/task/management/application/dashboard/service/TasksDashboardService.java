package com.task.management.application.dashboard.service;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.projection.Page;
import com.task.management.application.shared.query.PagedQuery;
import com.task.management.application.dashboard.port.in.TasksDashboardUseCase;
import com.task.management.application.dashboard.port.out.TasksDashboardRepository;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;

import static com.task.management.domain.shared.validation.Validation.actorIdRequired;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class TasksDashboardService implements TasksDashboardUseCase {
    private final TasksDashboardRepository tasksDashboardRepository;

    public TasksDashboardService(TasksDashboardRepository tasksDashboardRepository) {
        this.tasksDashboardRepository = tasksDashboardRepository;
    }

    @Override
    public TasksSummary getAssignedToSummary(UserId actor) {
        actorIdRequired(actor);
        return tasksDashboardRepository.getAssignedToSummary(actor);
    }

    @Override
    public TasksSummary getOwnedBySummary(UserId actor) {
        actorIdRequired(actor);
        return tasksDashboardRepository.getOwnedBySummary(actor);
    }

    @Override
    public Page<DashboardTaskPreview> getAssignedTo(FindAssignedDashboardTasksQuery query) {
        queryRequired(query);
        return tasksDashboardRepository.getAssignedTo(query);
    }

    @Override
    public Page<DashboardTaskPreview> getOwnedBy(FindOwnedDashboardTasksQuery query) {
        queryRequired(query);
        return tasksDashboardRepository.getOwnedBy(query);
    }

    private static void queryRequired(PagedQuery query) {
        parameterRequired(query, "Query");
    }
}
