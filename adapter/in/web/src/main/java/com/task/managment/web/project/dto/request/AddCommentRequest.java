package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
    @NotBlank(message = "Comments value is required")
    private String comment;
}
