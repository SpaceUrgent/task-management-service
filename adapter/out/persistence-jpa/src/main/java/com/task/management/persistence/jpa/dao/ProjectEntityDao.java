package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskStatusEntity;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectEntityDao extends EntityDao<ProjectEntity, Long> {

    Stream<ProjectEntity> findByMemberId(Long memberId);

    List<TaskStatusEntity> findAvailableTaskStatuses(Long projectId);
}
