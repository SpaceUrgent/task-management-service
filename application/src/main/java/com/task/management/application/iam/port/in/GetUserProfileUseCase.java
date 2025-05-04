package com.task.management.application.iam.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.UserInfo;

public interface GetUserProfileUseCase {
    UserInfo getUserProfile(UserId actorId) throws UseCaseException;
}
