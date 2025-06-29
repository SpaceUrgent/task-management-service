package com.task.management.persistence.jpa.dao;

public interface TaskNumberSequenceDao {

    Long nextNumber(Long projectId);
}
