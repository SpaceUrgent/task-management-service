package com.task.management.persistence.jpa.repository;

import com.task.management.persistence.jpa.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
