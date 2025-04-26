package com.task.management.domain.iam.model;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;


@Data
public class User {
    private final UserId id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private Email email;
    private String firstName;
    private String lastName;
    private String encryptedPassword;

    @Builder
    public User(UserId id,
                Instant createdAt,
                Instant updatedAt,
                Email email,
                String firstName,
                String lastName,
                String encryptedPassword) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at value");
        this.updatedAt = updatedAt;
        this.email = emailRequired(email);
        this.firstName = notBlank(firstName, "First name");
        this.lastName = notBlank(lastName, "Last name");
        this.encryptedPassword = notBlank(encryptedPassword, "Encrypted password");
    }
}
