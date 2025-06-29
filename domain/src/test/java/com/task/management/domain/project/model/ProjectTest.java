package com.task.management.domain.project.model;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.validation.ValidationException;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(new ProjectId(new Random().nextLong()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .title("Project A")
                .description("Test project")
                .ownerId(new UserId(new Random().nextLong()))
                .availableTaskStatuses(new ArrayList<>() {{
                    add(new TaskStatus("TODO", 1));
                    add(new TaskStatus("IN_PROGRESS", 2));
                    add(new TaskStatus("DONE", 3));
                }})
                .build();
    }

    @Test
    void addStatus_shouldInsertAtMiddleAndShiftOthers() {
        TaskStatus newStatus = new TaskStatus("REVIEW", 3);

        project.addStatus(newStatus);

        List<TaskStatus> statuses = project.getAvailableTaskStatuses();
        assertEquals(4, statuses.size());
        assertEquals("TODO", statuses.get(0).name());
        assertEquals("IN_PROGRESS", statuses.get(1).name());
        assertEquals("REVIEW", statuses.get(2).name());
        assertEquals("DONE", statuses.get(3).name());

        assertEquals(1, statuses.get(0).position());
        assertEquals(2, statuses.get(1).position());
        assertEquals(3, statuses.get(2).position());
        assertEquals(4, statuses.get(3).position());
    }

    @Test
    void addStatus_shouldThrowIfNameAlreadyExists() {
        TaskStatus duplicate = new TaskStatus("TODO", 1);

        assertThrows(ValidationException.class, () -> project.addStatus(duplicate));
    }

    @Test
    void removeStatus_shouldDeleteAndReorderPositions() {
        project.removeStatus("IN_PROGRESS");

        List<TaskStatus> statuses = project.getAvailableTaskStatuses();
        assertEquals(2, statuses.size());

        assertEquals("TODO", statuses.get(0).name());
        assertEquals(1, statuses.get(0).position());
        assertEquals("DONE", statuses.get(1).name());
        assertEquals(2, statuses.get(1).position());
    }

    @Test
    void removeStatus_shouldDoNothingIfStatusNotFound() {
        project.removeStatus("UNKNOWN");

        List<TaskStatus> statuses = project.getAvailableTaskStatuses();
        assertEquals(3, statuses.size()); // unchanged
    }

}