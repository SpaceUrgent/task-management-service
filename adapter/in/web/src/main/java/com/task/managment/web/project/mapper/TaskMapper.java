package com.task.managment.web.project.mapper;

import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.managment.web.common.mapper.UserInfoMapper;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.TaskPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
                .assignee(userInfoMapper.toDto(taskPreview.assignee()))
                .build();
    }

    public TaskDetailsDto toDto(TaskDetails taskDetails) {
        return TaskDetailsDto.builder()
                .id(taskDetails.id().value())
                .createdAt(format(taskDetails.createdAt()))
                .updatedAt(format(taskDetails.updatedAt()))
                .dueDate(taskDetails.dueDate())
                .number(taskDetails.number().value())
                .title(taskDetails.title())
                .description(taskDetails.description())
                .projectId(taskDetails.projectId().value())
                .status(taskDetails.status())
                .assignee(userInfoMapper.toDto(taskDetails.assignee()))
                .owner(userInfoMapper.toDto(taskDetails.owner()))
                .build();
    }

    private String format(Instant source) {
        return Optional.ofNullable(source)
                .map(instant -> instant.atZone(ZoneId.systemDefault()))
                .map(zonedDateTime -> zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .orElse(null);
    }
}
