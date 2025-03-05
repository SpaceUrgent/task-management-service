package com.task.managment.web.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

@Data
@NoArgsConstructor
public class TaskPreviewDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    private String title;
    private String status;
    private ProjectUserDto assignee;

    @Builder
    public TaskPreviewDto(Long id,
                          Instant createdAt,
                          String title,
                          String status,
                          ProjectUserDto assignee) {
        this.id = requireNonNull(id, "Id is required");
        this.createdAt = requireNonNull(createdAt, "Created at is required");
        this.title = requireNonNull(title, "Title is required");
        this.status = requireNonNull(status, "Status is required");
        this.assignee = requireNonNull(assignee, "Assignee is required");
    }
}
