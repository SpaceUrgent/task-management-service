package io.spaceurgent.server.application.user.port.in;

import io.spaceurgent.server.application.user.exception.UserExistsException;
import io.spacurgent.server.domain.user.User;

public interface RegisterUserUseCase {

    User register(RegisterCommand command) throws UserExistsException;
}
