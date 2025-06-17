package com.task.managment.web.project.dto;

import com.task.managment.web.shared.dto.UserInfoDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
@NoArgsConstructor
public class TaskPreviewDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    private Instant updatedAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
    private Long number;
    private String title;
    private String status;
    private String priority;
    private UserInfoDto assignee;

    @Builder
    public TaskPreviewDto(Long id,
                          Instant createdAt,
                          Instant updatedAt,
                          LocalDate dueDate,
                          Long number,
                          String title,
                          String status,
                          String priority,
                          UserInfoDto assignee) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.number = parameterRequired(number, "Number");
        this.title = notBlank(title, "Title");
        this.status = parameterRequired(status, "Status");
        this.priority = parameterRequired(priority, "Priority");
        this.assignee = parameterRequired(assignee, "Assignee");
    }
}
