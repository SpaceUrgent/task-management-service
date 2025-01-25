package com.task.management.application.service;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

import java.util.Random;

public final class TestUtils {
    private TestUtils() {
    }

    public static final String EMAIL = "test@example.com";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final char[] PASSWORD = "password123".toCharArray();
    public static final String ENCRYPTED_PASSWORD = "encryptedPassword";

    public static User getTestUser() {
        return User.builder()
                .id(randomUserId())
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .encryptedPassword(ENCRYPTED_PASSWORD)
                .build();
    }

    public static UserId randomUserId() {
        return new UserId(randomLong());
    }

    public static long randomLong() {
        return new Random().nextLong();
    }
}
