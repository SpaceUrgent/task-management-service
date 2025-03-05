package com.task.managment.web.dto;

import com.task.management.application.project.model.ProjectUser;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaskDetailsDto {
    private Long id;
    private Instant createdAt;
    private Long projectId;
    private String title;
    private String description;
    private String status;
    private ProjectUserDto owner;
    private ProjectUserDto assignee;
}
