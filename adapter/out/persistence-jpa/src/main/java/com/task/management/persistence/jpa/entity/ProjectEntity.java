package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "projects")
public class ProjectEntity extends JpaEntity<Long> {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "projects_members",
            joinColumns = @JoinColumn(name = "project_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "member_id", nullable = false)
    )
    private List<UserEntity> members = new ArrayList<>();

    protected ProjectEntity() {
    }

    @Builder
    public ProjectEntity(Long id,
                         Instant createdAt,
                         Instant updatedAt,
                         String title,
                         String description,
                         UserEntity owner,
                         List<UserEntity> members) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.owner = parameterRequired(owner, "Owner");
        this.members = Optional.ofNullable(members).orElse(new ArrayList<>());
    }

    public void addMember(UserEntity member) {
        parameterRequired(member, "Member");
        this.members.add(member);
        member.getProjects().add(this);
    }
}
