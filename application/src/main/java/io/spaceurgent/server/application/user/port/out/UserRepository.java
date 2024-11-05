package io.spaceurgent.server.application.user.port.out;

import io.spacurgent.server.domain.user.User;

public interface UserRepository {
    User save(User user);

    boolean emailExists(String email);
}
