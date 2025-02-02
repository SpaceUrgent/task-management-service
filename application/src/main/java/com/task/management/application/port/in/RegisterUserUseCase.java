package com.task.management.application.port.in;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.port.in.dto.RegisterUserDto;

public interface RegisterUserUseCase {
    UserDTO register(RegisterUserDto registerUserDto) throws EmailExistsException;
}
