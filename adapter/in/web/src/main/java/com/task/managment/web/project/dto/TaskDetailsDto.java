package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class TaskDetailsDto {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Long projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private ProjectUserDto owner;
    private ProjectUserDto assignee;

    @Builder
    public TaskDetailsDto(Long id,
                          Instant createdAt,
                          Long projectId,
                          String title,
                          String description,
                          TaskStatus status,
                          ProjectUserDto owner,
                          ProjectUserDto assignee) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.projectId = parameterRequired(projectId, "Project id");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
    }
}
