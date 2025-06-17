package com.task.management.application.common.port.out;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;

import java.util.Optional;

public interface UserInfoRepositoryPort {
    Optional<UserInfo> find(UserId id);

    Optional<UserInfo> find(Email email);
}
