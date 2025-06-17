package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.TaskNumberSequenceDao;
import com.task.management.persistence.jpa.entity.TaskNumberSequence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class TaskNumberSequenceDaoImpl implements TaskNumberSequenceDao {
    private final EntityManager entityManager;

    public TaskNumberSequenceDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long nextNumber(Long projectId) {
        parameterRequired(projectId, "Project id");
        var sequence = this.get(projectId);
        final var number = sequence.nextValue();
        entityManager.merge(sequence);
        return number;
    }

    private TaskNumberSequence get(Long projectId) {
        return entityManager.find(TaskNumberSequence.class, projectId, LockModeType.PESSIMISTIC_WRITE);
    }
}
