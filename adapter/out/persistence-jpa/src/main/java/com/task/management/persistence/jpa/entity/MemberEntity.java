package com.task.management.persistence.jpa.entity;

import com.task.management.domain.project.model.objectvalue.MemberRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
@Entity
@Table(name = "projects_members")
public class MemberEntity {
    @EmbeddedId
    private MemberPK id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private UserEntity user;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    protected MemberEntity() {
    }

    @Builder
    public MemberEntity(UserEntity userEntity,
                        ProjectEntity projectEntity,
                        MemberRole role) {
        this.id = new MemberPK(projectEntity.getId(), userEntity.getId());
        this.user = userEntity;
        this.project = projectEntity;
        this.role = role;
    }

    @Data
    @Embeddable
    public static class MemberPK implements Serializable {
        @Column(name = "project_id")
        private Long projectId;

        @Column(name = "member_id")
        private Long memberId;

        protected MemberPK() {
        }

        public MemberPK(Long projectId, Long userId) {
            this.projectId = projectId;
            this.memberId = parameterRequired(userId, "User id");
        }
    }
}
