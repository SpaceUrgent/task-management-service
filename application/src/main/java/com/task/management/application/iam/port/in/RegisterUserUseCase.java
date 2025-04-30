package com.task.management.application.iam.port.in;

import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command) throws EmailExistsException;
}
