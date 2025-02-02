package com.task.management.application.port.in;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EntityNotFoundException;

public interface GetUserByEmailUseCase {
    UserDTO getUser(String email) throws EntityNotFoundException;
}
