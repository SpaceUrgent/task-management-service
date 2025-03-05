package com.task.managment.web.mapper;

import com.task.management.application.project.model.TaskDetails;
import com.task.managment.web.dto.TaskDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskDetailsDtoMapper {
    private final ProjectUserDtoMapper projectUserDtoMapper;

    public TaskDetailsDto toDto(TaskDetails taskDetails) {
        return TaskDetailsDto.builder()
                .id(taskDetails.id().value())
                .createdAt(taskDetails.createdAt())
                .title(taskDetails.title())
                .description(taskDetails.description())
                .projectId(taskDetails.projectId().value())
                .status(taskDetails.status().value())
                .assignee(projectUserDtoMapper.toDto(taskDetails.assignee()))
                .owner(projectUserDtoMapper.toDto(taskDetails.owner()))
                .build();
    }
}
