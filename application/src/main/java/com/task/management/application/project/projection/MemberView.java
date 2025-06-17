package com.task.management.application.project.projection;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import lombok.Builder;

import static com.task.management.domain.shared.validation.Validation.emailRequired;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
