package com.task.management.application.port.in;

import com.task.management.application.exception.UserNotFoundException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

public interface GetUserUseCase {
    User getUser(UserId id) throws UserNotFoundException;
}
