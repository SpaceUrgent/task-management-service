package com.task.management.domain.iam.port.in;

import com.task.management.domain.iam.application.EmailExistsException;
import com.task.management.domain.iam.application.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command) throws EmailExistsException;
}
