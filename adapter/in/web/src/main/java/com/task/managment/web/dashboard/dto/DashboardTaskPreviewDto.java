package com.task.managment.web.dashboard.dto;

import com.task.managment.web.common.dto.UserInfoDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class DashboardTaskPreviewDto {
    private Instant createdAt;
    private Long taskId;
    private Long number;
    private String title;
    private Long projectId;
    private String projectTitle;
    private LocalDate dueDate;
    private Boolean isOverdue;
    private String priority;
    private String status;
    private UserInfoDto assignee;

    @Builder
    public DashboardTaskPreviewDto(Instant createdAt,
                                   Long taskId,
                                   Long number,
                                   String title,
                                   Long projectId,
                                   String projectTitle,
                                   LocalDate dueDate,
                                   Boolean isOverdue,
                                   String priority,
                                   String status,
                                   UserInfoDto assignee) {
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.taskId = parameterRequired(taskId, "Task id");
        this.number = parameterRequired(number, "Task number");
        this.title = parameterRequired(title, "Title");
        this.projectId = parameterRequired(projectId, "Project id");
        this.projectTitle = parameterRequired(projectTitle, "Project title");
        this.dueDate = dueDate;
        this.isOverdue = parameterRequired(isOverdue, "Is overdue");
        this.priority = parameterRequired(priority, "Priority");
        this.status = parameterRequired(status, "Status");
        this.assignee = parameterRequired(assignee, "Assignee");
    }
}
