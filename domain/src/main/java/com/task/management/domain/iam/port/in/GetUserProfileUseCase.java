package com.task.management.domain.iam.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;

public interface GetUserProfileUseCase {
    UserProfile getUserProfile(UserId actorId) throws UseCaseException;
}
