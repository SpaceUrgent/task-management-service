package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.view.TasksSummaryView;

import java.util.stream.Stream;

public interface TaskEntityDao extends EntityDao<TaskEntity, Long> {
    Stream<TaskEntity> findAllByAssigneeIdAndProjectId(Long assigneeId, Long projectId);

    boolean existsWithProjectIdAndStatus(Long projectId, String statusName);

    TasksSummaryView getSummaryByAssigneeId(Long assigneeId);

    TasksSummaryView getSummaryByOwnerId(Long ownerId);

    void removeAssignee(Long assigneeId, Long projectId);

}
