package com.task.management.domain.common;

import com.task.management.domain.iam.model.UserId;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record UserCredentials(UserId id,
                              Email email,
                              String encryptedPassword) {

    public UserCredentials {
        parameterRequired(id, "User id");
        emailRequired(email);
        notBlank(encryptedPassword, "Encrypted password");
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "email='" + email + '\'' +
                '}';
    }
}
