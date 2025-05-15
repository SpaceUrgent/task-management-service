package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskPriorityDto {
    private String name;
    private Integer order;
}
