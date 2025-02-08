package com.task.management.application.iam.model;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class User {
    private final UserId id;
    private String email;
    private String firstName;
    private String lastName;
    private String encryptedPassword;

    @Builder
    public User(UserId id,
                String email,
                String firstName,
                String lastName,
                String encryptedPassword) {
        this.id = id;
        this.email = requireNonNull(email, "Email is required");
        this.firstName = requireNonNull(firstName, "First name is required");
        this.lastName = requireNonNull(lastName, "Last name is required");
        this.encryptedPassword = requireNonNull(encryptedPassword, "Encrypted password is required");
    }
}
