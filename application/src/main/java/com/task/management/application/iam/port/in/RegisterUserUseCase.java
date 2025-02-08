package com.task.management.application.iam.port.in;

import com.task.management.application.iam.exception.EmailExistsException;
import com.task.management.application.iam.port.in.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command) throws EmailExistsException;
}
