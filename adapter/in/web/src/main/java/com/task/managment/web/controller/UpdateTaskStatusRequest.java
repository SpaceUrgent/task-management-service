package com.task.managment.web.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
}
