package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ToString.Exclude
    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    protected UserEntity() {
    }

    @Builder
    public UserEntity(Long id,
                      Instant createdAt,
                      Instant updatedAt,
                      String email,
                      String firstName,
                      String lastName,
                      String encryptedPassword) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.encryptedPassword = encryptedPassword;
    }
}
