package com.task.managment.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.dto.CreateProjectDto;
import com.task.management.application.dto.UpdateProjectDto;
import com.task.managment.web.WebTest;
import com.task.managment.web.WebTestConfiguration;
import com.task.managment.web.dto.PageDTO;
import com.task.managment.web.security.MockUser;
import com.task.managment.web.security.SecurityConfiguration;
import com.task.managment.web.security.UserDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Random;

import static com.task.managment.web.TestUtils.EMAIL;
import static com.task.managment.web.TestUtils.FIRST_NAME;
import static com.task.managment.web.TestUtils.LAST_NAME;
import static com.task.managment.web.TestUtils.randomProjectDTOs;
import static com.task.managment.web.TestUtils.randomProjectDetailsDTO;
import static com.task.managment.web.TestUtils.randomProjectUserDTO;
import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(controllerClass = ProjectController.class)
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
    @MockBean
    private GetProjectDetailsUseCase getProjectDetailsUseCase;
    @MockBean
    private UpdateProjectUseCase updateProjectUseCase;
    @MockBean
    private AddProjectMemberUseCase addProjectMemberByEmailUseCase;

    @MockUser
    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var givenRequest = getCreateProjectDto();
        final var expectedOwnerId = new UserId(DEFAULT_USER_ID_VALUE);
        final var expectedOwner = ProjectUserDTO.builder()
                .id(DEFAULT_USER_ID_VALUE)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        final var expectedProject = ProjectDTO.builder()
                .id(randomLong())
                .title(givenRequest.getTitle())
                .description(givenRequest.getDescription())
                .owner(expectedOwner)
                .build();
        doReturn(expectedProject).when(createProjectUseCase).createProject(eq(expectedOwnerId), eq(givenRequest));
        final var apiActionResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(givenRequest)))
                .andExpect(status().isCreated());
        assertMatches(expectedProject, apiActionResult);
    }

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnDefaultProjectPage_whenNoRequestParamsPresent() throws Exception {
        final var expectedPageNumber = 1;
        final var expectedPageSize = 20;
        final var expectedProjects = randomProjectDTOs(expectedPageSize);
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
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDTO<ProjectDTO>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertEquals(expectedProjects.get(i), resultData.get(i));
        }
    }

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnAppropriateProjectPage_whenPageParamsPresent() throws Exception {
        final var expectedPageNumber = 3;
        final var expectedPageSize = 5;
        final var expectedProjects = randomProjectDTOs(expectedPageSize);
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
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDTO<ProjectDTO>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertEquals(expectedProjects.get(i), resultData.get(i));
        }
    }

    @MockUser
    @Test
    void getProjectDetails_shouldReturnProjectDetails_whenAllConditionsMet() throws Exception {
        final var expectedProjectDetails = randomProjectDetailsDTO();
        final var givenProjectId = expectedProjectDetails.id();
        doReturn(expectedProjectDetails).when(getProjectDetailsUseCase)
                .getProjectDetails(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(new ProjectId(givenProjectId)));
        final var responseBody = mockMvc.perform(get("/api/projects/{givenProjectId}", givenProjectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var projectDetailsReceived = objectMapper.readValue(responseBody, ProjectDetailsDTO.class);
        assertEquals(expectedProjectDetails, projectDetailsReceived);
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnUpdatedProject_whenAllConditionsMet() throws Exception {
        final var givenUpdateDto = getUpdateProjectDto();
        final var expectedProject = ProjectDTO.builder()
                .id(randomLong())
                .title(givenUpdateDto.getTitle())
                .description(givenUpdateDto.getDescription())
                .owner(randomProjectUserDTO())
                .build();
        final var givenProjectId = expectedProject.id();
        doReturn(expectedProject).when(updateProjectUseCase)
                        .updateProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(new ProjectId(givenProjectId)), eq(givenUpdateDto));
        final var apiActionResult = mockMvc.perform(patch("/api/projects/{projectId}", givenProjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenUpdateDto)))
                .andExpect(status().isOk());
        assertMatches(expectedProject, apiActionResult);
    }

    @MockUser
    @Test
    void addProjectMember_shouldAddMember_whenAllConditionsMet() throws Exception {
        final var givenProjectId = randomProjectId();
        final var givenMemberId = randomLong();
        mockMvc.perform(put("/api/projects/{projectId}/members/{memberId}", givenProjectId.value(), givenMemberId))
                .andExpect(status().isOk());

        verify(addProjectMemberByEmailUseCase)
                .addMember(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(new UserId(givenMemberId)));
    }

    private void assertMatches(ProjectDTO expectedProject, ResultActions apiActionResult) throws Exception {
        final var expectedOwner = expectedProject.owner();
        apiActionResult.andExpect(jsonPath("$.id").value(expectedProject.id()))
                .andExpect(jsonPath("$.title").value(expectedProject.title()))
                .andExpect(jsonPath("$.description").value(expectedProject.description()))
                .andExpect(jsonPath("$.owner.id").value(expectedOwner.id()))
                .andExpect(jsonPath("$.owner.email").value(expectedOwner.email()))
                .andExpect(jsonPath("$.owner.firstName").value(expectedOwner.firstName()))
                .andExpect(jsonPath("$.owner.lastName").value(expectedOwner.lastName()));
    }

    private static UpdateProjectDto getUpdateProjectDto() {
        final var updateDto = new UpdateProjectDto();
        updateDto.setTitle("New title");
        updateDto.setDescription("New Description");
        return updateDto;
    }

    private static CreateProjectDto getCreateProjectDto() {
        final var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private static Long randomLong() {
        return new Random().nextLong();
    }
}