package com.task.managment.web.project.dto;

import com.task.management.domain.common.validation.Validation;
import com.task.management.domain.project.model.TaskStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;
import static java.util.Objects.requireNonNull;

@Data
@NoArgsConstructor
public class TaskPreviewDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    private String title;
    private TaskStatus status;
    private ProjectUserDto assignee;

    @Builder
    public TaskPreviewDto(Long id,
                          Instant createdAt,
                          String title,
                          TaskStatus status,
                          ProjectUserDto assignee) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.title = notBlank(title, "Title");
        this.status = parameterRequired(status, "Status");
        this.assignee = parameterRequired(assignee, "Assignee");
    }
}
