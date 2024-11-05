package io.spaceurgent.server.application.user.port.out;

public interface HashPasswordPort {
    String hash(byte[] password);
}
