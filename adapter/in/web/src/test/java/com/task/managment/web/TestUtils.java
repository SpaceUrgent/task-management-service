package com.task.managment.web;

import com.task.management.application.project.projection.MemberView;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.application.iam.projection.UserCredentials;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

import java.util.Random;

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

    public final static UserId USER_ID = new UserId(DEFAULT_USER_ID_VALUE);

    public static MemberView actingMemberView() {
        return MemberView.builder()
                .id(USER_ID)
                .role(MemberRole.ADMIN)
                .email(new Email("actor@domain.com"))
                .fullName("Acting User")
                .build();
    }

    public static MemberView randomMemberView() {
        final var idValue = randomLong();
        return MemberView.builder()
                .id(new UserId(idValue))
                .email(new Email("user-%d@mail.com".formatted(idValue)))
                .fullName("FName LName")
                .role(MemberRole.ADMIN)
                .build();
    }

    public static UserInfo randomUserInfo() {
        final var idValue = randomLong();
        return UserInfo.builder()
                .id(new UserId(idValue))
                .email(new Email("user-%d@mail.com".formatted(idValue)))
                .firstName("FName-%d".formatted(idValue))
                .lastName("LName-%d".formatted(idValue))
                .build();
    }

    public static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    public static UserId randomUserId() {
        return new UserId(randomLong());
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }
}
