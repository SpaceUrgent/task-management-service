package com.task.managment.web;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;

public final class TestUtils {
    private TestUtils() {
    }

    public final static UserId DEFAULT_USER_ID = new UserId(DEFAULT_USER_ID_VALUE);
    public final static String EMAIL = "test@domain.com";
    public final static String FIRST_NAME = "John";
    public final static String LAST_NAME = "Doe";
    public final static String PASSWORD = "password123";
    public final static String ENCRYPTED_PASSWORD = "encryptedPassword";

    public final static User DEFAULT_USER = User.builder()
            .id(DEFAULT_USER_ID)
            .email(EMAIL)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .encryptedPassword(ENCRYPTED_PASSWORD)
            .build();
}
