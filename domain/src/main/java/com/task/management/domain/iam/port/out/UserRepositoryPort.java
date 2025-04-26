package com.task.management.domain.iam.port.out;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.iam.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<UserInfo> find(UserId id);

    boolean emailExists(Email email);
}
