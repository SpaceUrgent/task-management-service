package com.task.management.domain.project.model;

import com.task.management.domain.common.Email;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class Member {
    private final MemberId id;
    private final ProjectId projectId;
    private final Email email;
    private final String fullName;
    private MemberRole role;

    @Builder
    public Member(MemberId id,
                  ProjectId projectId,
                  Email email,
                  String fullName,
                  MemberRole role) {
        this.id = parameterRequired(id, "Member id");
        this.projectId = parameterRequired(projectId, "Project id");
        this.email = emailRequired(email);
        this.fullName = parameterRequired(fullName, "Full name");
        this.role = role;
    }

    public boolean isOwner() {
        return MemberRole.OWNER == this.role;
    }

    public boolean isAdmin() {
        return MemberRole.ADMIN == this.role;
    }
}
