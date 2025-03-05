package com.task.management.domain.iam.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

@Data
public class User {
    private final UserId id;
    private final Instant createdAt;
    private String email;
    private String firstName;
    private String lastName;
    private String encryptedPassword;

    @Builder
    public User(UserId id,
                Instant createdAt,
                String email,
                String firstName,
                String lastName,
                String encryptedPassword) {
        this.id = id;
        this.createdAt = requireNonNull(createdAt, "Create at is required");
        this.email = requireNonNull(email, "Email is required");
        this.firstName = requireNonNull(firstName, "First name is required");
        this.lastName = requireNonNull(lastName, "Last name is required");
        this.encryptedPassword = requireNonNull(encryptedPassword, "Encrypted password is required");
    }
}
