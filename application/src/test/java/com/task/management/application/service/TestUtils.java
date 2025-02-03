package com.task.management.application.service;

import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.TaskId;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    public static void assertMatches(Project expected, ProjectDTO actual) {
        assertEquals(expected.getId().value(), actual.id());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertMatches(expected.getOwner(), actual.owner());
    }

    public static void assertMatches(ProjectUser expected, ProjectUserDTO actual) {
        assertEquals(expected.id().value(), actual.id());
        assertEquals(expected.email(), actual.email());
        assertEquals(expected.firstName(), actual.firstName());
        assertEquals(expected.lastName(), actual.lastName());
    }

    public static ProjectDetailsDTO randomProjectDetailsDTO() {
        final var projectIdValue = randomLong();
        final var ownerIdValue = randomLong();
        final var owner = ProjectUserDTO.builder()
                .id(ownerIdValue)
                .email("user%d@mail.com".formatted(ownerIdValue))
                .firstName("FName-%d".formatted(ownerIdValue))
                .lastName("LName-%d".formatted(ownerIdValue))
                .build();
        return ProjectDetailsDTO.builder()
                .id(projectIdValue)
                .title("Project-%d".formatted(projectIdValue))
                .description("Project-%d description".formatted(projectIdValue))
                .owner(owner)
                .members(List.of(owner))
                .build();
    }

    public static List<Project> randomProjects(int total) {
        return IntStream.range(0, total)
                .mapToObj(value -> randomProject())
                .toList();
    }

    public static Project randomProject() {
        final var projectIdValue = randomLong();
        return Project.builder()
                .id(new ProjectId(projectIdValue))
                .title("Project-%d title".formatted(projectIdValue))
                .description("Project-%d description".formatted(projectIdValue))
                .owner(randomProjectUser())
                .build();
    }

    public static ProjectUser randomProjectUser() {
        final var userIdValue = randomLong();
        return ProjectUser.builder()
                .id(new UserId(userIdValue))
                .email("user%d@mail.com".formatted(userIdValue))
                .firstName("FName-%d".formatted(userIdValue))
                .lastName("LName-%d".formatted(userIdValue))
                .build();
    }

    public static UserId randomUserId() {
        return new UserId(randomLong());
    }

    public static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    public static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }

    public static long randomLong() {
        return new Random().nextLong();
    }
}
