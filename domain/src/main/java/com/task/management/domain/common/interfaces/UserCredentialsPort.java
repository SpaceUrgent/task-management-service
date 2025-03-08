package com.task.management.domain.common.interfaces;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.UserCredentials;

import java.util.Optional;

public interface UserCredentialsPort {
    Optional<UserCredentials> findByEmail(Email email);
}
