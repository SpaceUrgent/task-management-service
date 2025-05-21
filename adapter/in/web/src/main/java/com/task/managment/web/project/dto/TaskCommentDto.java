package com.task.managment.web.project.dto;

import com.task.managment.web.common.dto.UserInfoDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class TaskCommentDto {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;
    private UserInfoDto author;
    private String content;

    @Builder
    public TaskCommentDto(Long id, Instant createdAt, UserInfoDto author, String content) {
        this.id = id;
        this.createdAt = createdAt;
        this.author = author;
        this.content = content;
    }
}
