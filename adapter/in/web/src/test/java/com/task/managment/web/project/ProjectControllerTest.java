package com.task.managment.web.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.common.projection.Page;
import com.task.management.application.common.query.Sort;
import com.task.management.application.project.RemoveTaskStatusException;
import com.task.management.application.project.command.*;
import com.task.management.application.project.port.in.*;
import com.task.management.application.project.projection.MemberView;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.objectvalue.*;
import com.task.managment.web.common.dto.ListResponse;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.common.dto.UserInfoDto;
import com.task.managment.web.project.dto.*;
import com.task.managment.web.project.dto.request.*;
import com.task.managment.web.common.dto.PagedResponse;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static com.task.managment.web.TestUtils.*;
import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ComponentScan(basePackages = {
        "com.task.managment.web.common.mapper",
        "com.task.managment.web.project.mapper"
})
@WebTest(testClasses = ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    @MockBean
    private GetProjectDetailsUseCase getProjectDetailsUseCase;
    @MockBean
    private CreateProjectUseCase createProjectUseCase;
    @MockBean
    private UpdateProjectUseCase updateProjectUseCase;
    @MockBean
    private AddProjectMemberUseCase addProjectMemberUseCase;
    @MockBean
    private UpdateMemberRoleUseCase updateMemberRoleUseCase;
    @MockBean
    private FindTasksUseCase findTasksUseCase;
    @MockBean
    private CreateTaskUseCase createTaskUseCase;

    @MockUser
    @Test
    void getAvailableProjects() throws Exception {
        final var expectedProjects = randomProjectPreviews();
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(eq(new UserId(DEFAULT_USER_ID_VALUE)));
        final var responseBody = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<ListResponse<ProjectPreviewDto>>() {
        });
        assertMatches(expectedProjects, result.getData());
    }

    @MockUser
    @Test
    void getProjectDetails() throws Exception {
        final var projectDetails = projectDetails();
        final var givenProjectId = projectDetails.id();
        doReturn(projectDetails).when(getProjectDetailsUseCase).getProjectDetails(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}", givenProjectId.value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, UserProjectDetailsDto.class);
        assertMatches(projectDetails, result);
    }

    @MockUser
    @Test
    void createProject() throws Exception {
        final var givenRequest = getCreateProjectRequest();
        mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());
        verify(createProjectUseCase).createProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), argThat(createProjectCommandMatcher(givenRequest)));
    }

    @MockUser
    @Test
    void updateProject() throws Exception {
        final var givenRequest = getUpdateProjectRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = UpdateProjectCommand.builder()
                .title(givenRequest.getTitle())
                .description(givenRequest.getDescription())
                .build();
        mockMvc.perform(put("/api/projects/{projectId}", givenProjectId.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isOk());
        verify(updateProjectUseCase).updateProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(expectedCommand));
    }

    @MockUser
    @Test
    void addMember() throws Exception {
        final var givenRequest = getAddMemberRequest();
        final var givenProjectId = randomProjectId();
        final var expectedEmail = new Email(givenRequest.getEmail());
        mockMvc.perform(post("/api/projects/{projectId}/members", givenProjectId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isOk());
        verify(addProjectMemberUseCase).addMember(eq(USER_ID), eq(givenProjectId), eq(expectedEmail));
    }

    @MockUser
    @Test
    void updateMemberRole() throws Exception {
        final var givenRequest = getUpdateMemberRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = UpdateMemberRoleCommand.builder()
                .projectId(givenProjectId)
                .memberId(new UserId(givenRequest.getMemberId()))
                .role(givenRequest.getRole())
                .build();
        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isOk());
        verify(updateMemberRoleUseCase).updateMemberRole(eq(USER_ID), eq(expectedCommand));
    }

    @MockUser
    @Test
    void addAvailableTaskStatus() throws Exception {
        final var givenRequest = getAddStatusRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = AddTaskStatusCommand.builder()
                .name(givenRequest.getName())
                .position(givenRequest.getPosition())
                .build();
        mockMvc.perform(put("/api/projects/{projectId}/available-statuses", givenProjectId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isOk());
        verify(updateProjectUseCase).addTaskStatus(eq(USER_ID), eq(givenProjectId), eq(expectedCommand));
    }

    @MockUser
    @Test
    void removeAvailableTaskStatus_ok() throws Exception {
        final var givenProjectId = randomProjectId();
        final var givenStatusName = "Review";
        mockMvc.perform(delete("/api/projects/{projectId}/available-statuses/{statusName}", givenProjectId.value(), givenStatusName))
                .andExpect(status().isOk());
        verify(updateProjectUseCase).removeTaskStatus(eq(USER_ID), eq(givenProjectId), eq(givenStatusName));
    }

    @MockUser
    @Test
    void removeAvailableTaskStatus_returnsConflict() throws Exception {
        final var givenProjectId = randomProjectId();
        final var givenStatusName = "Review";
        final var exception = new RemoveTaskStatusException("Failed to remove status");
        doThrow(exception).when(updateProjectUseCase).removeTaskStatus(eq(USER_ID), eq(givenProjectId), eq(givenStatusName));

        mockMvc.perform(delete("/api/projects/{projectId}/available-statuses/{statusName}", givenProjectId.value(), givenStatusName))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Conflict raised during request processing"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/projects/%d/available-statuses/%s".formatted(givenProjectId.value(), givenStatusName)));
    }

    @MockUser
    @Test
    void getTasks_withDefaultParams() throws Exception {
        final var givenProjectId = randomProjectId();
        final var expectedQuery = FindTasksQuery.builder()
                .pageNumber(1)
                .pageSize(50)
                .projectId(givenProjectId)
                .sortByCreatedAt(Sort.Direction.DESC)
                .build();
        final var expectedTaskPreviews = Page.<TaskPreview>builder()
                .pageNo(expectedQuery.getPageNumber())
                .pageSize(expectedQuery.getPageSize())
                .total(100)
                .totalPages(2)
                .content(randomTaskPreviews(expectedQuery.getPageSize()))
                .build();
        doReturn(expectedTaskPreviews).when(findTasksUseCase).findTasks(eq(USER_ID), eq(expectedQuery));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}/tasks", givenProjectId.value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PagedResponse<TaskPreviewDto>>() {});
        assertMatches(expectedTaskPreviews, result);
    }

    @MockUser
    @Test
    void getTasks_withCustomParams() throws Exception {
        final var givenProjectId = randomProjectId();
        final var givenAssigneeId = randomUserId();
        final var givenTaskStatus = Set.of("Done", "In progress");
        final var expectedQuery = FindTasksQuery.builder()
                .pageNumber(2)
                .pageSize(10)
                .projectId(givenProjectId)
                .assigneeId(givenAssigneeId)
                .statusIn(givenTaskStatus)
                .sortByCreatedAt(Sort.Direction.ASC)
                .build();
        final var totalTasks = 100;
        final var expectedTaskPreviews = Page.<TaskPreview>builder()
                .pageNo(expectedQuery.getPageNumber())
                .pageSize(expectedQuery.getPageSize())
                .total(totalTasks)
                .totalPages(totalTasks / expectedQuery.getPageSize())
                .content(randomTaskPreviews(expectedQuery.getPageSize()))
                .build();
        doReturn(expectedTaskPreviews).when(findTasksUseCase).findTasks(eq(USER_ID), eq(expectedQuery));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}/tasks", givenProjectId.value())
                        .param("page", "2")
                        .param("size", "10")
                        .param("assigneeId", givenAssigneeId.value().toString())
                        .param("status", givenTaskStatus.toArray(new String[]{}))
                        .param("sortBy", "createdAt:ASC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PagedResponse<TaskPreviewDto>>() {});
        assertMatches(expectedTaskPreviews, result);
    }

    @MockUser
    @Test
    void createTask() throws Exception {
        final var givenRequest = getCreateTaskRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = CreateTaskCommand.builder()
                .title(givenRequest.getTitle())
                .description(givenRequest.getDescription())
                .assigneeId(new UserId(givenRequest.getAssigneeId()))
                .dueDate(givenRequest.getDueDate())
                .build();

        mockMvc.perform(post("/api/projects/{projectId}/tasks", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());

        verify(createTaskUseCase).createTask(eq(USER_ID), eq(givenProjectId), eq(expectedCommand));
    }

    private void assertMatches(Page<TaskPreview> expected, PagedResponse<TaskPreviewDto> actual) {
        assertEquals(expected.pageNo(), actual.getCurrentPage());
        assertEquals(expected.pageSize(), actual.getPageSize());
        assertEquals(expected.total().longValue(), actual.getTotal());
        assertEquals(expected.totalPages().longValue(), actual.getTotalPages());
        for (int i = 0; i < expected.content().size(); i++) {
            assertMatches(expected.content().get(i), actual.getData().get(i));
        }
    }

    private void assertMatches(TaskPreview expected, TaskPreviewDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.createdAt(), actual.getCreatedAt());
        assertEquals(expected.updatedAt(), actual.getUpdatedAt());
        assertEquals(expected.dueDate(), actual.getDueDate());
        assertEquals(expected.number().value(), actual.getNumber());
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.status(), actual.getStatus());
        assertMatches(expected.assignee(), actual.getAssignee());
    }

    private void assertMatches(UserInfo expected, UserInfoDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.email().value(), actual.getEmail());
        assertEquals(expected.firstName(), actual.getFirstName());
        assertEquals(expected.lastName(), actual.getLastName());
        assertEquals(
                "%s %s".formatted(expected.firstName(), expected.lastName()),
                actual.getFullName()
        );
    }

    private CreateTaskRequest getCreateTaskRequest() {
        final var request = new CreateTaskRequest();
        request.setTitle("New task");
        request.setDescription("New task description");
        request.setAssigneeId(randomLong());
        request.setDueDate(LocalDate.now().plusDays(1));
        return request;
    }

    private AddTaskStatusRequest getAddStatusRequest() {
        final var request = new AddTaskStatusRequest();
        request.setName("Review");
        request.setPosition(3);
        return request;
    }

    private ProjectDetails projectDetails() {
        return ProjectDetails.builder()
                .id(randomProjectId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .title("Project title")
                .description("Project description")
                .owner(actingMemberView())
                .members(Set.of(actingMemberView()))
                .taskStatuses(List.of(
                        TaskStatus.builder()
                                .name("To do")
                                .position(1)
                                .build(),
                        TaskStatus.builder()
                                .name("Done")
                                .position(2)
                                .build()
                ))
                .build();
    }

    private void assertMatches(List<ProjectPreview> expected, List<ProjectPreviewDto> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(ProjectPreview expected, ProjectPreviewDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.title(), actual.getTitle());
        assertMatches(expected.owner(), actual.getOwner());
    }

    private void assertMatches(MemberView expected, MemberDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.email().value(), actual.getEmail());
        assertEquals(expected.fullName(), actual.getFullName());
        assertEquals(expected.role(), actual.getRole());
    }

    private void assertMatches(ProjectDetails expected, UserProjectDetailsDto actual) {
        assertEquals(actingMemberView().role(), actual.getRole());
        assertMatches(expected, actual.getProjectDetails());
        final var expectedTaskStatuses = expected.taskStatuses();
        final var actualTaskStatuses = actual.getProjectDetails().getTaskStatuses();
        assertEquals(expectedTaskStatuses.size(), actualTaskStatuses.size());
        IntStream.range(0, expectedTaskStatuses.size()).forEach(index -> {
            assertMatches(expectedTaskStatuses.get(index), actualTaskStatuses.get(index));
        });
    }

    private void assertMatches(TaskStatus expected, AvailableTaskStatusDto actual) {
        assertEquals(expected.name(), actual.getName());
        assertEquals(expected.position(), actual.getPosition());
    }

    private void assertMatches(ProjectDetails expected, ProjectDetailsDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.createdAt(), actual.getCreatedAt());
        assertEquals(expected.updatedAt(), actual.getUpdatedAt());
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.description(), actual.getDescription());
        assertMatches(expected.owner(), actual.getOwner());
        assertMatches(expected.members().toArray(new MemberView[]{}), actual.getMembers().toArray(new MemberDto[]{}));
    }

    private void assertMatches(MemberView[] expected, MemberDto[] actual) {
        for (int i = 0; i < expected.length; i++) {
            assertMatches(expected[i], actual[i]);
        }
    }

    private CreateProjectRequest getCreateProjectRequest() {
        final var request = new CreateProjectRequest();
        request.setTitle("New project");
        request.setDescription("New project description");
        return request;
    }

    private static UpdateProjectRequest getUpdateProjectRequest() {
        final var request = new UpdateProjectRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        return request;
    }

    private UpdateMemberRoleRequest getUpdateMemberRequest() {
        final var request = new UpdateMemberRoleRequest();
        request.setMemberId(randomLong());
        request.setRole(MemberRole.ADMIN);
        return request;
    }

    private List<ProjectPreview> randomProjectPreviews() {
        return IntStream.range(0, 20)
                .mapToObj(value -> randomProjectPreview())
                .toList();
    }

    private ProjectPreview randomProjectPreview() {
        final var randomProjectIdValue = randomLong();
        return ProjectPreview.builder()
                .id(new ProjectId(randomProjectIdValue))
                .title("Title %d".formatted(randomProjectIdValue))
                .owner(TestUtils.randomMemberView())
                .build();
    }

    private List<TaskPreview> randomTaskPreviews(int total) {
        return IntStream.range(0, total)
                .mapToObj(value -> randomTaskPreview())
                .toList();
    }

    private TaskPreview randomTaskPreview() {
        final var idValue = randomLong();
        return TaskPreview.builder()
                .id(new TaskId(idValue))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .dueDate(LocalDate.now().plusDays(10))
                .number(new TaskNumber(randomLong()))
                .title("Task %d".formatted(idValue))
                .status("In progress")
                .assignee(randomUserInfo())
                .build();
    }

    private AddProjectMemberRequest getAddMemberRequest() {
        AddProjectMemberRequest request = new AddProjectMemberRequest();
        request.setEmail("member@domain.com");
        return request;
    }

    private ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private static ArgumentMatcher<CreateProjectCommand> createProjectCommandMatcher(CreateProjectRequest expected) {
        return command -> Objects.equals(expected.getTitle(), command.title())
                && Objects.equals(expected.getDescription(), command.description());
    }
}