package com.task.management.persistence.jpa.entity;

import com.task.management.domain.common.validation.Validation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
@Table(name = "users")
public class UserEntity extends JpaEntity<Long> {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ToString.Exclude
    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @ManyToMany(mappedBy = "members")
    private List<ProjectEntity> projects = new ArrayList<>();

    protected UserEntity() {
    }

    @Builder
    public UserEntity(Long id,
                      Instant createdAt,
                      Instant updatedAt,
                      String email,
                      String firstName,
                      String lastName,
                      String encryptedPassword,
                      List<ProjectEntity> projects) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.email = notBlank(email, "Email");
        this.firstName = notBlank(firstName, "First name");
        this.lastName = notBlank(lastName, "Last name");
        this.encryptedPassword = notBlank(encryptedPassword, "Encrypted password");
        this.projects = Optional.ofNullable(projects).orElse(new ArrayList<>());
    }
}
