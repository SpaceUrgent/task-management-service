package com.task.managment.web.dto.response;

import com.task.managment.web.dto.ProjectPreviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProjectsResponse {
    private List<ProjectPreviewDto> data;
}
