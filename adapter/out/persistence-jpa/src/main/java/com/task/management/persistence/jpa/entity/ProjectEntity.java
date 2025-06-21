package com.task.management.persistence.jpa.entity;

import com.task.management.domain.project.model.objectvalue.MemberRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.*;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "projects")
public class ProjectEntity extends JpaEntity<Long> {

    @Column(nullable = false)
    private String title;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text")
    private String description;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    private TaskNumberSequence taskNumberSequence;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<MemberEntity> members = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskStatusEntity> availableTaskStatuses = new ArrayList<>();

    protected ProjectEntity() {
    }

    @Builder
    public ProjectEntity(Long id,
                         Instant createdAt,
                         Instant updatedAt,
                         String title,
                         String description,
                         List<MemberEntity> members) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.members = Optional.ofNullable(members).orElse(new ArrayList<>());
    }

    public MemberEntity getOwner() {
        return this.members.stream()
                .filter(member -> MemberRole.OWNER == member.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Project owner not found"));
    }

    public void addMember(UserEntity userEntity, MemberRole role) {
        final var newMember = MemberEntity.builder()
                .projectEntity(this)
                .userEntity(userEntity)
                .role(role)
                .build();
        this.members.add(newMember);
    }
}
