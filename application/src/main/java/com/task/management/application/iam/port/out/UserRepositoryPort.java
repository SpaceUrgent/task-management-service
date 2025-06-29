package com.task.management.application.iam.port.out;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.iam.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> find(UserId id);

    Optional<UserInfo> findUserInfo(UserId id);

    boolean emailExists(Email email);
}
