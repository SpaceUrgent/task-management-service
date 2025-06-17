package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
public class AvailableTaskStatusDto {
    private String name;
    private Integer position;

    @Builder
    public AvailableTaskStatusDto(String name,
                                  Integer position) {
        this.name = notBlank(name, "Name");
        this.position = parameterRequired(position, "Position");
    }
}
