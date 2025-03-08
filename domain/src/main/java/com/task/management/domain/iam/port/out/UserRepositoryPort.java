package com.task.management.domain.iam.port.out;

import com.task.management.domain.common.Email;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<UserProfile> findUserProfile(UserId id);

    boolean emailExists(Email email);
}
