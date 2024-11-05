package io.spacurgent.server.domain.user;

public record UserId(Long value) {
    public UserId {
        assert value != null : "User id value is required";
    }
}
