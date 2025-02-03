package com.task.management.application.dto;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String status;
    private ProjectUserDTO assignee;

    @Builder
    public TaskDTO(Long id,
                   String title,
                   String status,
                   ProjectUserDTO assignee) {
        this.id = requireNonNull(id, "Id is required");
        this.title = requireNonNull(title, "Id is required");
        this.status = requireNonNull(status, "Status is required");
        this.assignee = requireNonNull(assignee, "Assignee is required");
    }
}
