package com.task.management.application.iam.port.out;

import com.task.management.application.iam.model.UserCredentials;

import java.util.Optional;

public interface FindUserCredentialsPort {
    Optional<UserCredentials> findByEmail(String email);
}
