package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.TaskEntity;

public interface TaskEntityDao extends EntityDao<TaskEntity, Long> {

    boolean existsWithProjectIdAndStatus(Long projectId, String statusName);
}
