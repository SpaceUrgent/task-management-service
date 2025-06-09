package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.view.TasksSummaryView;

public interface TaskEntityDao extends EntityDao<TaskEntity, Long> {

    boolean existsWithProjectIdAndStatus(Long projectId, String statusName);

    TasksSummaryView getSummaryByAssigneeId(Long assigneeId);

    TasksSummaryView getSummaryByOwnerId(Long ownerId);
}
