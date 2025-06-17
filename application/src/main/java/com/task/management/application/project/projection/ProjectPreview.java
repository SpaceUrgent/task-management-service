package com.task.management.application.project.projection;

import com.task.management.domain.shared.model.objectvalue.ProjectId;
import lombok.Builder;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
