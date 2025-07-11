package com.task.management.application.dashboard.port.in;

import com.task.management.application.shared.projection.Page;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;

public interface TasksDashboardUseCase {
    TasksSummary getAssignedToSummary(UserId actor);
    TasksSummary getOwnedBySummary(UserId actor);
    Page<DashboardTaskPreview> getAssignedTo(FindAssignedDashboardTasksQuery query);
    Page<DashboardTaskPreview> getOwnedBy(FindOwnedDashboardTasksQuery query);
}
