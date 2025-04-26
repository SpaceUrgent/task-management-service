package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.TaskStatus;
import com.task.managment.web.common.dto.UserInfoDto;
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
    private Long number;
    private String title;
    private String description;
    private TaskStatus status;
    private UserInfoDto owner;
    private UserInfoDto assignee;

    @Builder
    public TaskDetailsDto(Long id,
                          Instant createdAt,
                          Instant updatedAt,
                          Long projectId,
                          Long number,
                          String title,
                          String description,
                          TaskStatus status,
                          UserInfoDto owner,
                          UserInfoDto assignee) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.projectId = parameterRequired(projectId, "Project id");
        this.number = parameterRequired(number, "Number");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
    }
}
