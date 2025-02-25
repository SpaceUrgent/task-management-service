package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "projects")
@Data
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

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
                         String title,
                         String description,
                         UserEntity owner,
                         List<UserEntity> members) {
        this.id = id;
        this.createdAt = requireNonNull(createdAt, "Created at is required");
        this.title = requireNonNull(title, "Title is required");
        this.description = requireNonNull(description, "Description is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.members = Optional.ofNullable(members).orElse(new ArrayList<>());
    }

    public void addMember(UserEntity member) {
        requireNonNull(member, "Member is required");
        this.members.add(member);
        member.getProjects().add(this);
    }
}
