package com.task.management.application.project;

import com.task.management.application.common.TestUtils;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.project.model.*;
import com.task.management.application.project.projection.MemberView;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public final class ProjectTestUtils {
    private ProjectTestUtils() {
    }

    public static List<MemberView> randomProjectUsers() {
        return IntStream.range(0, 10)
                .mapToObj(value -> randomMemberView())
                .toList();
    }

    public static <T> Answer<T> self(Class<T> selfClass) {
        return invocation ->  selfClass.cast(invocation.getArgument(0));
    }

    public static Task randomTask() {
        return Task.builder()
                .id(TestUtils.randomTaskId())
                .number(TestUtils.randomTaskNumber())
                .createdAt(Instant.now())
                .project(TestUtils.randomProjectId())
                .status("In progress")
                .title("Title")
                .description("Description")
                .owner(TestUtils.randomUserId())
                .assignee(TestUtils.randomUserId())
                .build();
    }

    public static MemberView randomMemberView() {
        return MemberView.builder()
                .id(TestUtils.randomUserId())
                .email(new Email("project-user@mail.com"))
                .fullName("Fname Lname")
                .build();
    }

    public static UserInfo randomUserInfo() {
        final var idValue = TestUtils.randomLong();
        return UserInfo.builder()
                .id(new UserId(idValue))
                .email(new Email("username@domain.com"))
                .firstName("FName-%d".formatted(idValue))
                .lastName("LName-%d".formatted(idValue))
                .build();
    }
}
