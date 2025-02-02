package com.task.managment.web;

import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.dto.UserDTO;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

import java.util.List;
import java.util.Random;
import java.util.Set;
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

    public final static User DEFAULT_USER = User.builder()
            .id(DEFAULT_USER_ID)
            .email(EMAIL)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .encryptedPassword(ENCRYPTED_PASSWORD)
            .build();

    public static List<ProjectDTO> randomProjectDTOs(int total) {
        return IntStream.range(0, total).mapToObj(value -> randomProjectDTO()).toList();
    }

    public static ProjectDetailsDTO randomProjectDetailsDTO() {
        final var projectId = randomLong();
        final var owner = randomProjectUserDTO();
        return ProjectDetailsDTO.builder()
                .id(projectId)
                .title("Project-%d".formatted(projectId))
                .description("Project-%d description".formatted(projectId))
                .owner(owner)
                .members(Set.of(owner))
                .build();
    }

    public static ProjectDTO randomProjectDTO() {
        final var projectId = randomLong();
        return ProjectDTO.builder()
                .id(projectId)
                .title("Project-%d".formatted(projectId))
                .description("Project-%d description".formatted(projectId))
                .owner(randomProjectUserDTO())
                .build();
    }

    public static ProjectUserDTO randomProjectUserDTO() {
        final var userId = randomLong();
        return ProjectUserDTO.builder()
                .id(userId)
                .email("user%d@mail.com".formatted(userId))
                .firstName("FName-%d".formatted(userId))
                .lastName("LName-%d".formatted(userId))
                .build();
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }
}
