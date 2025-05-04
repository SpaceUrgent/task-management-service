package com.task.management.application.iam.port.out;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.iam.model.objectvalue.UserCredentials;

import java.util.Optional;

public interface UserCredentialsPort {
    Optional<UserCredentials> findByEmail(Email email);
}
