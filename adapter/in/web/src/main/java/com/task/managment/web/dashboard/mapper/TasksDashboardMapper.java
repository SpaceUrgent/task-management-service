package com.task.managment.web.dashboard.mapper;

import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.managment.web.common.mapper.UserInfoMapper;
import com.task.managment.web.dashboard.dto.DashboardTaskPreviewDto;
import com.task.managment.web.dashboard.dto.TasksSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Component
@RequiredArgsConstructor
public class TasksDashboardMapper {
    private final UserInfoMapper userInfoMapper;

    public TasksSummaryDto toTasksSummaryDto(TasksSummary model) {
        parameterRequired(model, "Task summary");
        return TasksSummaryDto.builder()
                .total(model.total())
                .open(model.open())
                .overdue(model.overdue())
                .closed(model.closed())
                .build();
    }

    public List<DashboardTaskPreviewDto> toDashboardTaskPreviewDtoList(List<DashboardTaskPreview> modelList) {
        return modelList.stream()
                .map(this::toDashboardTaskPreviewDto)
                .toList();
    }

    public DashboardTaskPreviewDto toDashboardTaskPreviewDto(DashboardTaskPreview model) {
        parameterRequired(model, "Dashboard task preview");
        return DashboardTaskPreviewDto.builder()
                .createdAt(model.createdAt())
                .taskId(model.taskId().value())
                .number(model.number().value())
                .title(model.title())
                .projectId(model.projectId().value())
                .projectTitle(model.projectTitle())
                .dueDate(model.dueDate())
                .isOverdue(model.isOverdue())
                .priority(model.priority().priorityName())
                .status(model.status())
                .assignee(userInfoMapper.toDto(model.assignee()))
                .build();
    }
}
