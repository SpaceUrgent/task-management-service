package com.task.management.domain.project.service;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.projection.MemberView;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public final class ProjectTestUtils {
    private ProjectTestUtils() {
    }

//    static Answer<UserInfo> getProjectUserAnswer() {
//        return invocation -> {
//            final var id = (UserInfo) invocation.getArgument(0);
//            return ProjectUser.withId(id);
//        };
//    }

    static List<MemberView> randomProjectUsers() {
        return IntStream.range(0, 10)
                .mapToObj(value -> randomMemberView())
                .toList();
    }

    static <T> Answer<T> self(Class<T> selfClass) {
        return invocation ->  selfClass.cast(invocation.getArgument(0));
    }

    public static MemberView randomMemberView() {
        return MemberView.builder()
                .id(randomUserId())
                .email(new Email("project-user@mail.com"))
                .fullName("Fname Lname")
                .build();
    }

    public static UserInfo randomUserInfo() {
        return UserInfo.builder()
                .id(randomUserId())
                .email(new Email("username@domain.com"))
                .firstName("FName")
                .lastName("LName")
                .build();
    }

    static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }

    static TaskNumber randomTaskNumber() {
        return new TaskNumber(randomLong());
    }

    static UserId randomUserId() {
        return new UserId(randomLong());
    }

    static Long randomLong() {
        return new Random().nextLong();
    }
}
