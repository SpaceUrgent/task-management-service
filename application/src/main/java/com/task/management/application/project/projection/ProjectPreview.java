package com.task.management.application.project.projection;

import com.task.management.domain.project.model.ProjectId;
import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectPreview(
        ProjectId id,
        String title,
        MemberView owner
) {
    @Builder
    public ProjectPreview {
        parameterRequired(id,"Id");
        notBlank(title,"Title");
        parameterRequired(owner,"Owner");
    }
}
