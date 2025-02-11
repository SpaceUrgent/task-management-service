package com.task.management.application.project.model;

import lombok.Builder;

import static com.task.management.application.common.Validation.parameterRequired;

public record ProjectPreview(
        ProjectId id,
        String title,
        ProjectUser owner
) {
    @Builder
    public ProjectPreview {
        parameterRequired(id,"Id");
        parameterRequired(title,"Title");
        parameterRequired(owner,"Owner");
    }
}
