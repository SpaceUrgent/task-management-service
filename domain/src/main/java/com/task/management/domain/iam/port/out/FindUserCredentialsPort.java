package com.task.management.domain.iam.port.out;

import com.task.management.domain.iam.model.UserCredentials;

import java.util.Optional;

public interface FindUserCredentialsPort {
    Optional<UserCredentials> findByEmail(String email);
}
