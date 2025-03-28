package com.task.management.domain.project.model;

import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectPreview(
        ProjectId id,
        String title,
        ProjectUser owner
) {
    @Builder
    public ProjectPreview {
        parameterRequired(id,"Id");
        notBlank(title,"Title");
        parameterRequired(owner,"Owner");
    }
}
