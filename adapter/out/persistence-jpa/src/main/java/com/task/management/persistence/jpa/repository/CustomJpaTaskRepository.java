package com.task.management.persistence.jpa.repository;


import com.task.management.persistence.jpa.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CustomJpaTaskRepository {
    Page<TaskEntity> findPage(FindPageQuery<TaskEntity> query);


    interface FindTaskByProjectQuery {
        Long projectId();
        Set<String> statusIn();
        Long assigneeId();
        Pageable pageable();
    }
}
