package com.task.management.application.iam.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.iam.model.UserId;
import com.task.management.application.iam.model.UserProfile;

public interface GetUserProfileUseCase {
    UserProfile getUserProfile(UserId actorId) throws UseCaseException;
}
