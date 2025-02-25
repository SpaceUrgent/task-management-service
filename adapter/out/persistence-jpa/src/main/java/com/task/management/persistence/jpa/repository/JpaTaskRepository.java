package com.task.management.persistence.jpa.repository;

import com.task.management.persistence.jpa.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTaskRepository extends JpaRepository<TaskEntity, Long>,
                                           CustomJpaTaskRepository {
}
