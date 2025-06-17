package com.task.managment.web.project.dto;

import com.task.managment.web.common.dto.UserInfoDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
public class TaskDetailsDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
    private Long projectId;
    private Long number;
    private String title;
    private String description;
    private String status;
    private String priority;
    private UserInfoDto owner;
    private UserInfoDto assignee;
    private List<TaskChangeLogDto> changeLogs;
    private List<TaskCommentDto> comments;

    @Builder
    public TaskDetailsDto(Long id,
                          Instant createdAt,
                          Instant updatedAt,
                          LocalDate dueDate,
                          Long projectId,
                          Long number,
                          String title,
                          String description,
                          String status,
                          String priority,
                          UserInfoDto owner,
                          UserInfoDto assignee,
                          List<TaskChangeLogDto> changeLogs,
                          List<TaskCommentDto> comments) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.projectId = parameterRequired(projectId, "Project id");
        this.number = parameterRequired(number, "Number");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.priority = parameterRequired(priority, "Priority");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
        this.changeLogs = changeLogs;
        this.comments = comments;
    }
}
