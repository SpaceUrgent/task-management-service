package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.AvailableTaskStatus;
import com.task.management.persistence.jpa.entity.ProjectEntity;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectEntityDao extends EntityDao<ProjectEntity, Long> {

    Stream<ProjectEntity> findByMemberId(Long memberId);

    List<AvailableTaskStatus> findAvailableTaskStatuses(Long projectId);
}
