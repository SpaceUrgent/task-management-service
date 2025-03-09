package com.task.managment.web.project.mapper;

import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskPreview;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.TaskPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final ProjectUserMapper projectUserDtoMapper;

    public TaskPreviewDto toDto(TaskPreview taskPreview) {
        return TaskPreviewDto.builder()
                .id(taskPreview.id().value())
                .createdAt(taskPreview.createdAt())
                .title(taskPreview.title())
                .status(taskPreview.status())
                .assignee(projectUserDtoMapper.toDto(taskPreview.assignee()))
                .build();
    }

    public TaskDetailsDto toDto(TaskDetails taskDetails) {
        return TaskDetailsDto.builder()
                .id(taskDetails.id().value())
                .createdAt(taskDetails.createdAt())
                .title(taskDetails.title())
                .description(taskDetails.description())
                .projectId(taskDetails.projectId().value())
                .status(taskDetails.status())
                .assignee(projectUserDtoMapper.toDto(taskDetails.assignee()))
                .owner(projectUserDtoMapper.toDto(taskDetails.owner()))
                .build();
    }
}
