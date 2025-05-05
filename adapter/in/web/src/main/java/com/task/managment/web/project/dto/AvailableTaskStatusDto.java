package com.task.managment.web.project.dto;

import com.task.management.domain.common.validation.Validation;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

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
