package com.task.management.domain.project.model;

import com.task.management.domain.project.event.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


import static com.task.management.domain.project.ProjectTestUtils.randomTask;
import static com.task.management.domain.project.ProjectTestUtils.randomUserId;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void updateTitle_shouldCreateEvent_whenTitleChanged() {
        final var givenValue = "New title";
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var initialValue = task.getTitle();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateTitle(givenActorId, givenValue);

        assertNotEquals(initialUpdatedTime, task.getUpdatedAt());
        final var domainEvents = task.flushEvents();
        assertEquals(1, domainEvents.size());
        var event  = assertInstanceOf(TaskTitleUpdatedEvent.class, domainEvents.getFirst());
        assertNotNull(event.getOccurredAt());
        assertEquals(givenActorId, event.getActorId());
        assertEquals(task.getId(), event.getEntityId());
        assertEquals(initialValue, event.getInitialValue());
        assertEquals(givenValue, event.getNewValue());
    }

    @Test
    void updateTitle_shouldDoNothing_whenTitleWasNotChanged() {
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var givenValue = task.getTitle();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateTitle(givenActorId, givenValue);
        assertEquals(initialUpdatedTime, task.getUpdatedAt());

        assertTrue(task.flushEvents().isEmpty());
    }

    @Test
    void updateDescription_shouldCreateEvent_whenValueChanged() {
        final var givenValue = "New description";
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var initialValue = task.getDescription();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateDescription(givenActorId, givenValue);

        assertNotEquals(initialUpdatedTime, task.getUpdatedAt());
        final var domainEvents = task.flushEvents();
        assertEquals(1, domainEvents.size());
        var event  = assertInstanceOf(TaskDescriptionUpdatedEvent.class, domainEvents.getFirst());

        assertNotNull(event.getOccurredAt());
        assertEquals(givenActorId, event.getActorId());
        assertEquals(task.getId(), event.getEntityId());
        assertEquals(initialValue, event.getInitialValue());
        assertEquals(givenValue, event.getNewValue());
    }

    @Test
    void updateDescription_shouldDoNothing_whenValueWasNotChanged() {
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var givenValue = task.getTitle();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateTitle(givenActorId, givenValue);
        assertEquals(initialUpdatedTime, task.getUpdatedAt());

        assertTrue(task.flushEvents().isEmpty());
    }

    @Test
    void updateDueDate_shouldCreateEvent_whenValueChanged() {
        final var givenValue = LocalDate.now().plusMonths(2);
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var initialValue = task.getDueDate();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateDueDate(givenActorId, givenValue);

        assertNotEquals(initialUpdatedTime, task.getUpdatedAt());
        final var domainEvents = task.flushEvents();
        assertEquals(1, domainEvents.size());
        var event  = assertInstanceOf(TaskDueDateUpdatedEvent.class, domainEvents.getFirst());
        assertNotNull(event.getOccurredAt());
        assertEquals(givenActorId, event.getActorId());
        assertEquals(task.getId(), event.getEntityId());
        assertEquals(initialValue, event.getInitialValue());
        assertEquals(givenValue, event.getNewValue());
    }

    @Test
    void updateDueDate_shouldDoNothing_whenValueWasNotChanged() {
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var givenValue = task.getDueDate();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateDueDate(givenActorId, givenValue);
        assertEquals(initialUpdatedTime, task.getUpdatedAt());

        assertTrue(task.flushEvents().isEmpty());
    }

    @Test
    void updateStatus_shouldCreateEvent_whenValueChanged() {
        final var givenValue = TaskStatus.DONE;
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var initialValue = task.getStatus();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateStatus(givenActorId, givenValue);

        assertNotEquals(initialUpdatedTime, task.getUpdatedAt());
        final var domainEvents = task.flushEvents();
        assertEquals(1, domainEvents.size());
        var event  = assertInstanceOf(TaskStatusUpdatedEvent.class, domainEvents.getFirst());
        assertNotNull(event.getOccurredAt());
        assertEquals(givenActorId, event.getActorId());
        assertEquals(task.getId(), event.getEntityId());
        assertEquals(initialValue, event.getInitialValue());
        assertEquals(givenValue, event.getNewValue());
    }

    @Test
    void updateStatus_shouldDoNothing_whenValueWasNotChanged() {
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var givenValue = task.getStatus();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.updateStatus(givenActorId, givenValue);
        assertEquals(initialUpdatedTime, task.getUpdatedAt());

        assertTrue(task.flushEvents().isEmpty());
    }

    @Test
    void assignTo_shouldCreateEvent_whenValueChanged() {
        final var givenValue = randomUserId();
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var initialValue = task.getAssignee();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.assignTo(givenActorId, givenValue);

        assertNotEquals(initialUpdatedTime, task.getUpdatedAt());
        final var domainEvents = task.flushEvents();
        assertEquals(1, domainEvents.size());
        var event  = assertInstanceOf(TaskReassignedEvent.class, domainEvents.getFirst());
        assertNotNull(event.getOccurredAt());
        assertEquals(givenActorId, event.getActorId());
        assertEquals(task.getId(), event.getEntityId());
        assertEquals(initialValue, event.getInitialValue());
        assertEquals(givenValue, event.getNewValue());
    }

    @Test
    void assignTo_shouldDoNothing_whenValueWasNotChanged() {
        final var givenActorId = randomUserId();
        final var task = randomTask();
        final var givenValue = task.getAssignee();
        final var initialUpdatedTime = task.getUpdatedAt();

        task.assignTo(givenActorId, givenValue);
        assertEquals(initialUpdatedTime, task.getUpdatedAt());

        assertTrue(task.flushEvents().isEmpty());
    }
}