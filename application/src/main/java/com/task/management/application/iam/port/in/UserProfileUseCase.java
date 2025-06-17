package com.task.management.application.iam.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.command.UpdatePasswordCommand;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;

public interface UserProfileUseCase {
    UserInfo getUserProfile(UserId actorId) throws UseCaseException;

    void updateName(UserId actorId, UpdateNameCommand command) throws UseCaseException;

    void updatePassword(UserId actorId, UpdatePasswordCommand command) throws UseCaseException;
}
