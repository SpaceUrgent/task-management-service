package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.TaskStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Set;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class ProjectDetailsDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;
    private String title;
    private String description;
    private ProjectUserDto owner;
    private Set<TaskStatus> taskStatuses;
    private Set<ProjectUserDto> members;

    @Builder
    public ProjectDetailsDto(Long id,
                             Instant createdAt,
                             Instant updatedAt,
                             String title,
                             String description,
                             ProjectUserDto owner,
                             Set<TaskStatus> taskStatuses,
                             Set<ProjectUserDto> members) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.owner = parameterRequired(owner, "Owner");
        this.taskStatuses = parameterRequired(taskStatuses, "Status set");
        this.members = parameterRequired(members, "Member set");
    }
}
