package com.task.management.application.project.projection;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.MemberRole;
import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record MemberView(
        UserId id,
        Email email,
        String fullName,
        MemberRole role
) {
    @Builder
    public MemberView {
        parameterRequired(id, "Member id");
        emailRequired(email);
        parameterRequired(fullName, "Full name");
    }
}
