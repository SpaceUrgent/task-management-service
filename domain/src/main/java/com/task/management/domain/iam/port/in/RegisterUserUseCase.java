package com.task.management.domain.iam.port.in;

import com.task.management.domain.iam.exception.EmailExistsException;
import com.task.management.domain.iam.port.in.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command) throws EmailExistsException;
}
