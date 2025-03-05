package com.task.management.domain.iam.port.out;

import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;

import java.util.Optional;

public interface FindUserProfileByIdPort {
    Optional<UserProfile> find(UserId id);
}
