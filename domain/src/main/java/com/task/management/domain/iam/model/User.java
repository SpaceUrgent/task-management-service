package com.task.management.domain.iam.model;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;


@Data
@Setter(value = AccessLevel.PROTECTED)
public class User {
    private final UserId id;
    private final Instant createdAt;
    private Instant updatedAt;
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
        this.firstName = firstNameRequired(firstName);
        this.lastName = lastNameRequired(lastName);
        this.encryptedPassword = encryptedPasswordRequired(encryptedPassword);
    }

    public void updateName(String firstName, String lastName) {
        recordUpdatedTime();
        this.firstName = firstNameRequired(firstName);
        this.lastName = lastNameRequired(lastName);
    }

    public void updatePassword(String encryptedPassword) {
        recordUpdatedTime();
        this.encryptedPassword = encryptedPasswordRequired(encryptedPassword);
    }

    private void recordUpdatedTime() {
        this.updatedAt = Instant.now();
    }

    private static String firstNameRequired(String firstName) {
        return notBlank(firstName, "First name");
    }

    private static String lastNameRequired(String lastName) {
        return notBlank(lastName, "Last name");
    }

    private static String encryptedPasswordRequired(String encryptedPassword) {
        return notBlank(encryptedPassword, "Encrypted password");
    }
}
