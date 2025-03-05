package com.task.managment.web.mapper;

import com.task.management.domain.project.model.TaskPreview;
import com.task.managment.web.dto.TaskPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskPreviewDtoMapper {
    private final ProjectUserDtoMapper projectUserDtoMapper;

    public TaskPreviewDto toDto(TaskPreview taskPreview) {
        return TaskPreviewDto.builder()
                .id(taskPreview.id().value())
                .createdAt(taskPreview.createdAt())
                .title(taskPreview.title())
                .status(taskPreview.status().value())
                .assignee(projectUserDtoMapper.toDto(taskPreview.assignee()))
                .build();
    }
}
