package com.task.management.application.port.in;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.model.User;
import com.task.management.application.port.in.dto.RegisterUserDto;

public interface RegisterUserUseCase {
    User register(RegisterUserDto registerUserDto) throws EmailExistsException;
}
