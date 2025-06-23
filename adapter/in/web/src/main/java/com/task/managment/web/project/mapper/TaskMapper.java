package com.task.managment.web.project.mapper;

import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskCommentView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import com.task.managment.web.shared.dto.UserInfoDto;
import com.task.managment.web.shared.mapper.UserInfoMapper;
import com.task.managment.web.project.dto.TaskChangeLogDto;
import com.task.managment.web.project.dto.TaskCommentDto;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.TaskPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final UserInfoMapper userInfoMapper;

    public TaskPreviewDto toDto(TaskPreview taskPreview) {
        return TaskPreviewDto.builder()
                .id(taskPreview.id().value())
                .createdAt(taskPreview.createdAt())
                .updatedAt(taskPreview.updatedAt())
                .dueDate(taskPreview.dueDate())
                .number(taskPreview.number().value())
                .title(taskPreview.title())
                .status(taskPreview.status())
                .priority(taskPreview.priority().priorityName())
                .assignee(mapAssignee(taskPreview.assignee()))
                .build();
    }

    public TaskDetailsDto toDto(TaskDetails taskDetails) {
        return TaskDetailsDto.builder()
                .id(taskDetails.id().value())
                .createdAt(taskDetails.createdAt())
                .updatedAt(taskDetails.updatedAt())
                .dueDate(taskDetails.dueDate())
                .number(taskDetails.number().value())
                .title(taskDetails.title())
                .description(taskDetails.description())
                .projectId(taskDetails.projectId().value())
                .status(taskDetails.status())
                .priority(taskDetails.priority().priorityName())
                .assignee(mapAssignee(taskDetails.assignee()))
                .owner(userInfoMapper.toDto(taskDetails.owner()))
                .changeLogs(toDtos(taskDetails.changeLogs()))
                .comments(toDto(taskDetails.comments()))
                .build();
    }

    private UserInfoDto mapAssignee(UserInfo assignee) {
        return Optional.ofNullable(assignee).map(userInfoMapper::toDto).orElse(null);
    }

    private List<TaskChangeLogDto> toDtos(List<TaskChangeLogView> taskChangeLogViews) {
        return taskChangeLogViews.stream()
                .map(this::toDto)
                .toList();
    }

    public TaskChangeLogDto toDto(TaskChangeLogView changeLog) {
        return TaskChangeLogDto.builder()
                .occurredAt(changeLog.time())
                .logMessage(mapLogMessage(changeLog))
                .oldValue(mapLogPropertyValue(changeLog.targetProperty(), changeLog.initialValue()))
                .newValue(mapLogPropertyValue(changeLog.targetProperty(), changeLog.newValue()))
                .build();
    }

    private List<TaskCommentDto> toDto(List<TaskCommentView> comments) {
        return comments.stream()
                .map(this::toDto)
                .toList();
    }

    private TaskCommentDto toDto(TaskCommentView comment) {
        return TaskCommentDto.builder()
                .id(comment.id().value())
                .createdAt(comment.createdAt())
                .author(userInfoMapper.toDto(comment.author()))
                .content(comment.content())
                .build();
    }

    private String mapLogMessage(TaskChangeLogView changeLog) {
        final var taskProperty = changeLog.targetProperty();
        final var actionDescription = switch (taskProperty) {
            case TITLE -> "title";
            case DESCRIPTION -> "description";
            case DUE_DATE -> "due date";
            case ASSIGNEE -> "assignee";
            case STATUS -> "status";
            case PRIORITY -> "priority";
        };
        final var actorFullName = Optional.ofNullable(changeLog.actor()).map(UserInfo::fullName).orElse("");
        return "%s updated %s".formatted(actorFullName, actionDescription).trim();
    }

    private String mapLogPropertyValue(TaskProperty taskProperty, String value) {
        if (TaskProperty.ASSIGNEE == taskProperty) {
            return Optional.ofNullable(value).orElse("Unassigned");
        }
        return value;
    }
}
