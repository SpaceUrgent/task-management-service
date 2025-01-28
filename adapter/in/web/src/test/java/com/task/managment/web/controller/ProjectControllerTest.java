package com.task.managment.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.common.PageQuery;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.managment.web.WebTest;
import com.task.managment.web.dto.PageDto;
import com.task.managment.web.dto.ProjectDto;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest
class ProjectControllerTest {
    private final static String PROJECT_TITLE = "Project title";
    private final static String PROJECT_DESCRIPTION = "Project description";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateProjectUseCase createProjectUseCase;
    @MockBean
    private GetAvailableProjectsUseCase getAvailableProjectsUseCase;

    @MockUser
    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var createProjectDto = getCreateProjectDto();
        final var expectedOwnerId = new UserId(DEFAULT_USER_ID_VALUE);
        final var expectedProject = getTestProject(expectedOwnerId);
        doReturn(expectedProject).when(createProjectUseCase).createProject(eq(expectedOwnerId), eq(createProjectDto));
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedProject.getId().value()))
                .andExpect(jsonPath("$.title").value(expectedProject.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedProject.getDescription()));
    }

    @MockUser
    @Test
    void createProject_shouldReturnBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/projects"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Required request body is missing"))
                .andExpect(jsonPath("$.path").value("/api/projects"));
    }

    @MockUser
    @Test
    void createProject_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .content(objectMapper.writeValueAsBytes(new CreateProjectDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Request validation error"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.description").value("Description is required"))
                .andExpect(jsonPath("$.path").value("/api/projects"));
    }

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnDefaultProjectPage_whenNoRequestParamsPresent() throws Exception {
        final var expectedPageNumber = 1;
        final var expectedPageSize = 20;
        final var expectedProjects = getTestProjects(expectedPageSize);
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(
                        eq(new UserId(DEFAULT_USER_ID_VALUE)),
                        eq(new PageQuery(expectedPageNumber, expectedPageSize))
                );
        final var responseBody = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDto<ProjectDto>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertMatches(expectedProjects.get(i), resultData.get(i));
        }
    }

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnAppropriateProjectPage_whenPageParamsPresent() throws Exception {
        final var expectedPageNumber = 3;
        final var expectedPageSize = 5;
        final var expectedProjects = getTestProjects(expectedPageSize);
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(
                        eq(new UserId(DEFAULT_USER_ID_VALUE)),
                        eq(new PageQuery(expectedPageNumber, expectedPageSize))
                );
        final var responseBody = mockMvc.perform(get("/api/projects")
                        .param("pageNumber", String.valueOf(expectedPageNumber))
                        .param("pageSize", String.valueOf(expectedPageSize)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDto<ProjectDto>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertMatches(expectedProjects.get(i), resultData.get(i));
        }
    }

    private void assertMatches(Project expected, ProjectDto actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private static CreateProjectDto getCreateProjectDto() {
        final var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static List<Project> getTestProjects(int size) {
        return IntStream.range(0, size).mapToObj(value -> getTestProject()).toList();
    }

    private static Project getTestProject() {
        return getTestProject(new UserId(new Random().nextLong()));
    }

    private static Project getTestProject(final UserId expectedOwnerId) {
        final var projectId = new ProjectId(new Random().nextLong());
        return Project.builder()
                .id(projectId)
                .title("Project %d".formatted(projectId.value()))
                .description("Project %d description".formatted(projectId.value()))
                .owner(expectedOwnerId)
                .build();
    }

}