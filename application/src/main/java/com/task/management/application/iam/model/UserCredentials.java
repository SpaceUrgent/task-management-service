package com.task.management.application.iam.model;

import static java.util.Objects.requireNonNull;

public record UserCredentials(UserId id,
                              String email,
                              String encryptedPassword) {

    public UserCredentials {
        requireNonNull(email, "Email is required");
        requireNonNull(encryptedPassword, "Encrypted password is required");
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "email='" + email + '\'' +
                '}';
    }
}
