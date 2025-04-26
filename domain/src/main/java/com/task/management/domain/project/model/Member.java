package com.task.management.domain.project.model;

import com.task.management.domain.common.model.UserId;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class Member {
    private final UserId id;
    private final ProjectId projectId;
    private MemberRole role;

    @Builder
    public Member(UserId id,
                  ProjectId projectId,
                  MemberRole role) {
        this.id = parameterRequired(id, "Member id");
        this.projectId = parameterRequired(projectId, "Project id");
        this.role = role;
    }

    public boolean isOwner() {
        return MemberRole.OWNER == this.role;
    }
}
