package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;
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
    private MemberDto owner;
    private List<AvailableTaskStatusDto> taskStatuses;
    private List<TaskPriorityDto> taskPriorities;
    private Set<MemberDto> members;

    @Builder
    public ProjectDetailsDto(Long id,
                             Instant createdAt,
                             Instant updatedAt,
                             String title,
                             String description,
                             MemberDto owner,
                             List<AvailableTaskStatusDto> taskStatuses,
                             List<TaskPriorityDto> taskPriorities,
                             Set<MemberDto> members) {
        this.id = parameterRequired(id, "Id");
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.owner = parameterRequired(owner, "Owner");
        this.taskStatuses = parameterRequired(taskStatuses, "Status set");
        this.taskPriorities = parameterRequired(taskPriorities, "Task priorities");
        this.members = parameterRequired(members, "Member set");
    }
}
