package com.task.managment.web.controller;

import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.managment.web.dto.ProjectDto;
import com.task.managment.web.mapper.WebProjectMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final CreateProjectUseCase createProjectUseCase;
    private final WebProjectMapper projectMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid @NotNull CreateProjectDto createProjectDto) {
        final var project = createProjectUseCase.createProject(currentUser().getId(), createProjectDto);
        return projectMapper.toDto(project);
    }

    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
