package com.task.management.application.project;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.*;
import com.task.management.application.project.projection.MemberView;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.List;
import java.util.Random;
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
                .id(randomTaskId())
                .number(randomTaskNumber())
                .createdAt(Instant.now())
                .project(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomUserId())
                .assignee(randomUserId())
                .build();
    }

    public static MemberView randomMemberView() {
        return MemberView.builder()
                .id(randomUserId())
                .email(new Email("project-user@mail.com"))
                .fullName("Fname Lname")
                .build();
    }

    public static UserInfo randomUserInfo() {
        final var idValue = randomLong();
        return UserInfo.builder()
                .id(new UserId(idValue))
                .email(new Email("username@domain.com"))
                .firstName("FName-%d".formatted(idValue))
                .lastName("LName-%d".formatted(idValue))
                .build();
    }

    public static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    public static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }

    public static TaskNumber randomTaskNumber() {
        return new TaskNumber(randomLong());
    }

    public static UserId randomUserId() {
        return new UserId(randomLong());
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }
}
