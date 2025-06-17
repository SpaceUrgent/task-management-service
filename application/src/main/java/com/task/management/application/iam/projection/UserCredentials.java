package com.task.management.application.iam.projection;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;

import java.io.Serializable;

import static com.task.management.domain.shared.validation.Validation.emailRequired;
import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record UserCredentials(UserId id,
                              Email email,
                              String encryptedPassword) implements Serializable {

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
