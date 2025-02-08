package com.task.management.application.iam.port.out;

import com.task.management.application.iam.model.UserId;
import com.task.management.application.iam.model.UserProfile;

import java.util.Optional;

public interface FindUserProfileByIdPort {
    Optional<UserProfile> find(UserId id);
}
