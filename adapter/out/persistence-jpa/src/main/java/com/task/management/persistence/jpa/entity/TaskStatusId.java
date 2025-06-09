package com.task.management.persistence.jpa.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class TaskStatusId implements Serializable {
    private Long project;
    private String name;
}
