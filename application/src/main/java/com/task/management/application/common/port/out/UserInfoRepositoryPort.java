package com.task.management.application.common.port.out;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.UserInfo;

import java.util.Optional;

public interface UserInfoRepositoryPort {
    Optional<UserInfo> find(UserId id);

    Optional<UserInfo> find(Email email);
}
