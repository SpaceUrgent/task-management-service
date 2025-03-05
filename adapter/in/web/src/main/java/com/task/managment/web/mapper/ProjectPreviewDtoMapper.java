package com.task.managment.web.mapper;

import com.task.management.application.project.model.ProjectPreview;
import com.task.managment.web.dto.ProjectPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectPreviewDtoMapper {
    private final ProjectUserDtoMapper projectUserDtoMapper;

    public ProjectPreviewDto toDto(ProjectPreview projectPreview) {
        return ProjectPreviewDto.builder()
                .id(projectPreview.id().value())
                .title(projectPreview.title())
                .owner(projectUserDtoMapper.toDto(projectPreview.owner()))
                .build();
    }
}
