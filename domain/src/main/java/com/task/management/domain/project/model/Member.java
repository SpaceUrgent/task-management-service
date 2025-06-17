package com.task.management.domain.project.model;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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

    public static Member create(UserId userId, ProjectId projectId) {
        return Member.builder()
                .id(userId)
                .projectId(projectId)
                .build();
    }

    public boolean isOwnerOrAdmin() {
        return isOwner() || isAdmin();
    }

    public boolean isOwner() {
        return MemberRole.OWNER == this.role;
    }

    private boolean isAdmin() {
        return MemberRole.ADMIN == this.role;
    }
}
