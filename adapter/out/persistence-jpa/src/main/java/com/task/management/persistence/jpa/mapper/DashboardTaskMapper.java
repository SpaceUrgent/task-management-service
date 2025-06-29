package com.task.management.persistence.jpa.mapper;

import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.entity.UserEntity;

import java.time.LocalDate;
import java.util.Optional;

import static com.task.management.domain.shared.model.objectvalue.TaskPriority.withOrder;
import static java.util.Objects.nonNull;

public class DashboardTaskMapper {
    public static final DashboardTaskMapper INSTANCE = new DashboardTaskMapper(UserInfoMapper.INSTANCE);

    private final UserInfoMapper userInfoMapper;

    private DashboardTaskMapper(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    public DashboardTaskPreview toDashboardTaskPreview(TaskEntity entity) {
        final var dueDate = entity.getDueDate();
        return DashboardTaskPreview.builder()
                .createdAt(entity.getCreatedAt())
                .taskId(new TaskId(entity.getId()))
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .projectId(new ProjectId(entity.getProject().getId()))
                .projectTitle(entity.getProject().getTitle())
                .dueDate(dueDate)
                .isOverdue(nonNull(dueDate) && dueDate.isBefore(LocalDate.now()))
                .priority(withOrder(entity.getPriority()))
                .status(entity.getStatusName())
                .assignee(mapAssignee(entity))
                .build();
    }

    private UserInfo mapAssignee(TaskEntity taskEntity) {
        return Optional.of(taskEntity)
                .map(TaskEntity::getAssignee)
                .map(userInfoMapper::toModel)
                .orElse(null);
    }
}
