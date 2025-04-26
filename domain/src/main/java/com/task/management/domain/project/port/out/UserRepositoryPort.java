package com.task.management.domain.project.port.out;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserInfo> find(UserId id);

    Optional<UserInfo> find(Email email);
}
