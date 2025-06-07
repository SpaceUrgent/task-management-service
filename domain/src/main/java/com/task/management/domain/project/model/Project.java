package com.task.management.domain.project.model;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.validation.ValidationException;
import com.task.management.domain.common.model.objectvalue.ProjectId;
import com.task.management.domain.common.model.objectvalue.TaskStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static com.task.management.domain.common.validation.Validation.*;

@Getter
@ToString
@EqualsAndHashCode
public class Project {
    private final ProjectId id;
    private final Instant createdAt;
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;
    private String title;
    private String description;
    private final List<TaskStatus> availableTaskStatuses;
    private final UserId ownerId;

    @Builder
    public Project(ProjectId id,
                   Instant createdAt,
                   Instant updatedAt,
                   String title,
                   String description,
                   List<TaskStatus> availableTaskStatuses,
                   UserId ownerId) {
        this.id = id;
        this.createdAt = parameterRequired(createdAt, "Created at");
        this.updatedAt = updatedAt;
        this.title = notBlank(title, "Title");
        this.description = description;
        this.availableTaskStatuses = uniqueStatusesRequired(availableTaskStatuses);
        this.ownerId = parameterRequired(ownerId, "Owner id");
    }

    public void updateTitle(String title) {
        recordUpdateTime();
        this.title = notBlank(title, "Title");
    }

    public void updateDescription(String description) {
        recordUpdateTime();
        this.description = description;
    }

    public void removeStatus(String statusName) {
        parameterRequired(statusName, "Status name");
        if (availableTaskStatuses.removeIf(status -> statusName.equalsIgnoreCase(status.name()))) {
            reorderAvailableStatuses();
        }
        if (availableTaskStatuses.isEmpty()) {
            throw new ValidationException("Project must has at least one available status");
        }
    }

    public void addStatus(TaskStatus status) {
        parameterRequired(status, "Task status");
        checkUniqueStatusName(status.name());
        int position = Math.min(status.position(), availableTaskStatuses.size());
        availableTaskStatuses.add(position - 1, status);
        reorderAvailableStatuses();
    }

    private void recordUpdateTime() {
        this.updatedAt = Instant.now();
    }

    private void checkUniqueStatusName(String name) {
        if (availableTaskStatuses.stream().anyMatch(taskStatus -> name.equalsIgnoreCase(taskStatus.name())))
            throw new ValidationException("Project already has status with name '%s'".formatted(name));
    }

    private void reorderAvailableStatuses() {
        availableTaskStatuses.sort(Comparator.comparing(TaskStatus::position));
        IntStream.range(0, availableTaskStatuses.size()).forEach(index ->
                availableTaskStatuses.set(index, availableTaskStatuses.get(index).changePosition(index + 1))
        );
    }

    private static List<TaskStatus> uniqueStatusesRequired(List<TaskStatus> statuses) {
        notEmpty(statuses, "Available task statuses");
        Set<String> names = new HashSet<>();
        Set<Integer> orders = new HashSet<>();
        statuses.forEach(status -> {
            if (!names.add(status.name())) throw new ValidationException("Duplicate status name is found '%s'".formatted(status.name()));
            if (!orders.add(status.position())) throw new ValidationException("Duplicate status name is found '%d'".formatted(status.position()));
        });
        return statuses;
    }
}
