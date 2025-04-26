package com.task.managment.web;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.iam.model.UserCredentials;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

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

    public final static UserCredentials DEFAULT_CREDENTIALS = new UserCredentials(DEFAULT_USER_ID, new Email(EMAIL), ENCRYPTED_PASSWORD);

    public final static ProjectUserId PROJECT_USER_ID = new ProjectUserId(DEFAULT_USER_ID_VALUE);

    public static List<ProjectUser> randomProjectUsers() {
        return IntStream.range(0, 20)
                .mapToObj(value -> randomProjectUser())
                .toList();
    }

    public static ProjectUser randomProjectUser() {
        final var idValue = randomLong();
        return ProjectUser.builder()
                .id(new ProjectUserId(idValue))
                .email(new Email("user-%d@mail.com".formatted(idValue)))
                .firstName("FName-%d".formatted(idValue))
                .lastName("LName-%d".formatted(idValue))
                .build();
    }

    public static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }
}
