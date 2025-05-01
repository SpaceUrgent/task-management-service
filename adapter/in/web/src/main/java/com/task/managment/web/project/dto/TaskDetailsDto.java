package com.task.managment.web.project.dto;

import com.task.management.domain.project.model.TaskStatus;
import com.task.managment.web.common.dto.UserInfoDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class TaskDetailsDto {
    private Long id;
    private String createdAt;
    private String updatedAt;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
    private Long projectId;
    private Long number;
    private String title;
    private String description;
    private TaskStatus status;
    private UserInfoDto owner;
    private UserInfoDto assignee;
    private List<TaskChangeLogDto> changeLogs;

    @Builder
    public TaskDetailsDto(Long id,
                          String createdAt,
                          String updatedAt,
                          LocalDate dueDate,
                          Long projectId,
                          Long number,
                          String title,
                          String description,
                          TaskStatus status,
                          UserInfoDto owner,
                          UserInfoDto assignee,
                          List<TaskChangeLogDto> changeLogs) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.projectId = parameterRequired(projectId, "Project id");
        this.number = parameterRequired(number, "Number");
        this.title = notBlank(title, "Title");
        this.description = description;
        this.status = parameterRequired(status, "Status");
        this.owner = parameterRequired(owner, "Owner");
        this.assignee = parameterRequired(assignee, "Assignee");
        this.changeLogs = changeLogs;
    }
}
