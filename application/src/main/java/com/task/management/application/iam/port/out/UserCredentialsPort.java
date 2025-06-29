package com.task.management.application.iam.port.out;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.application.iam.projection.UserCredentials;

import java.util.Optional;

public interface UserCredentialsPort {
    Optional<UserCredentials> findByEmail(Email email);
}
