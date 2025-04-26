package com.task.management.domain.iam.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;

public interface GetUserProfileUseCase {
    UserInfo getUserProfile(UserId actorId) throws UseCaseException;
}
