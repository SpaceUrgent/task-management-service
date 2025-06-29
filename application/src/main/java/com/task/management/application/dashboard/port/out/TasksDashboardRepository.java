package com.task.management.application.dashboard.port.out;

import com.task.management.application.shared.projection.Page;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;

public interface TasksDashboardRepository {
    TasksSummary getAssignedToSummary(UserId assignee);
    TasksSummary getOwnedBySummary(UserId owner);
    Page<DashboardTaskPreview> getAssignedTo(FindAssignedDashboardTasksQuery query);
    Page<DashboardTaskPreview> getOwnedBy(FindOwnedDashboardTasksQuery query);
}
