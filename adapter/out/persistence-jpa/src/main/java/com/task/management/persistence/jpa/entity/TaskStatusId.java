package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TaskStatusId implements Serializable {
    @Column(name = "project_id")
    private Long project;
    private String name;

    protected TaskStatusId() {
    }

    public TaskStatusId(Long project, String name) {
        this.project = project;
        this.name = name;
    }
}
