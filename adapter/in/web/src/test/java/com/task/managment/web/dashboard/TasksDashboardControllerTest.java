package com.task.managment.web.dashboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.shared.projection.Page;
import com.task.management.application.shared.query.Sort;
import com.task.management.application.dashboard.port.in.TasksDashboardUseCase;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.shared.model.objectvalue.*;
import com.task.managment.web.WebTest;
import com.task.managment.web.common.dto.PagedResponse;
import com.task.managment.web.dashboard.dto.DashboardTaskPreviewDto;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;
import java.util.stream.IntStream;

import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ComponentScan(basePackages = {
        "com.task.managment.web.common.mapper",
        "com.task.managment.web.dashboard.mapper"
})
@WebTest(testClasses = TasksDashboardController.class)
class TasksDashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TasksDashboardUseCase tasksDashboardUseCase;

    @MockUser
    @Test
    void getAssignedTasksSummary() throws Exception {
        final var expected = tasksSummary();
        doReturn(tasksSummary()).when(tasksDashboardUseCase).getAssignedToSummary(eq(new UserId(DEFAULT_USER_ID_VALUE)));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/assigned/summary"));
        assertOk(resultActions);
        assertSummaryMatched(expected, resultActions);
    }

    @MockUser
    @Test
    void getAssignedTasks_withDefaultParams() throws Exception {
        final var expectedQuery = FindAssignedDashboardTasksQuery.builder()
                .assignee(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(10)
                .pageNumber(1)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getAssignedTo(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/assigned"));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getAssignedTasks_withProvidedParams() throws Exception {
        final var expectedQuery = FindAssignedDashboardTasksQuery.builder()
                .assignee(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(20)
                .pageNumber(3)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getAssignedTo(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/assigned")
                .param("page", expectedQuery.getPageNumber().toString())
                .param("size", expectedQuery.getPageSize().toString()));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOverdueAssignedTasks_withDefaultParams() throws Exception {
        final var expectedQuery = FindAssignedDashboardTasksQuery.builder()
                .assignee(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(10)
                .pageNumber(1)
                .overdueOnly()
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getAssignedTo(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/assigned/overdue"));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOverdueAssignedTasks_withProvidedParams() throws Exception {
        final var expectedQuery = FindAssignedDashboardTasksQuery.builder()
                .assignee(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(41)
                .pageNumber(5)
                .overdueOnly()
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getAssignedTo(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/assigned/overdue")
                .param("page", expectedQuery.getPageNumber().toString())
                .param("size", expectedQuery.getPageSize().toString()));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOwnedTasksSummary() throws Exception {
        final var expected = tasksSummary();
        doReturn(tasksSummary()).when(tasksDashboardUseCase).getOwnedBySummary(eq(new UserId(DEFAULT_USER_ID_VALUE)));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/owned/summary"));
        assertOk(resultActions);
        assertSummaryMatched(expected, resultActions);
    }

    @MockUser
    @Test
    void getOwnedTasks_withDefaultParams() throws Exception {
        final var expectedQuery = FindOwnedDashboardTasksQuery.builder()
                .owner(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(10)
                .pageNumber(1)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getOwnedBy(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/owned"));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOwnedTasks_withProvidedParams() throws Exception {
        final var expectedQuery = FindOwnedDashboardTasksQuery.builder()
                .owner(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(20)
                .pageNumber(3)
                .sortBy("priority", Sort.Direction.DESC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .sortBy("dueDate", Sort.Direction.ASC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getOwnedBy(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/owned")
                .param("page", expectedQuery.getPageNumber().toString())
                .param("size", expectedQuery.getPageSize().toString()));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOverdueOwnedTasks_withDefaultParams() throws Exception {
        final var expectedQuery = FindOwnedDashboardTasksQuery.builder()
                .owner(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(10)
                .pageNumber(1)
                .overdueOnly()
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getOwnedBy(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/owned/overdue"));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    @MockUser
    @Test
    void getOverdueOwnedTasks_withProvidedParams() throws Exception {
        final var expectedQuery = FindOwnedDashboardTasksQuery.builder()
                .owner(new UserId(DEFAULT_USER_ID_VALUE))
                .pageSize(20)
                .pageNumber(3)
                .overdueOnly()
                .sortBy("dueDate", Sort.Direction.ASC)
                .sortBy("createdAt", Sort.Direction.DESC)
                .build();
        final var previewPage = dashboardTaskPreviewPage(expectedQuery.getPageNumber(), expectedQuery.getPageSize());
        doReturn(previewPage).when(tasksDashboardUseCase).getOwnedBy(eq(expectedQuery));
        final var resultActions = mockMvc.perform(get("/api/dashboard/tasks/owned/overdue")
                .param("page", expectedQuery.getPageNumber().toString())
                .param("size", expectedQuery.getPageSize().toString()));
        assertOk(resultActions);
        assertMatches(previewPage, resultActions);
    }

    private void assertMatches(Page<DashboardTaskPreview> expectedPage, ResultActions resultActions) throws Exception {
        assertContentTypeJson(resultActions);
        final var json = resultActions.andReturn().getResponse().getContentAsString();
        final var result = objectMapper.readValue(json, new TypeReference<PagedResponse<DashboardTaskPreviewDto>>() {});
        assertEquals(expectedPage.pageNo(), result.getCurrentPage());
        assertEquals(expectedPage.pageSize(), result.getPageSize());
        assertEquals(expectedPage.total().longValue(), result.getTotal());
        assertEquals(expectedPage.totalPages().longValue(), result.getTotalPages());
        IntStream.range(0, expectedPage.content().size()).forEach(index -> {
            assertMatches(expectedPage.content().get(index), result.getData().get(index));
        });
    }

    private void assertMatches(DashboardTaskPreview expected, DashboardTaskPreviewDto actual) {
        assertEquals(expected.createdAt(), actual.getCreatedAt());
        assertEquals(expected.taskId().value(), actual.getTaskId());
        assertEquals(expected.number().value(), actual.getNumber());
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.projectId().value(), actual.getProjectId());
        assertEquals(expected.projectTitle(), actual.getProjectTitle());
        assertEquals(expected.dueDate(), actual.getDueDate());
        assertEquals(expected.isOverdue(), actual.getIsOverdue());
        assertEquals(expected.priority().priorityName(), actual.getPriority());
        assertEquals(expected.status(), actual.getStatus());
        assertEquals(expected.assignee().id().value(), actual.getAssignee().getId());
    }

    private void assertSummaryMatched(TasksSummary expected, ResultActions resultActions) throws Exception {
        assertContentTypeJson(resultActions);
        resultActions
                .andExpect(jsonPath("$.total").value(expected.total()))
                .andExpect(jsonPath("$.open").value(expected.open()))
                .andExpect(jsonPath("$.overdue").value(expected.overdue()))
                .andExpect(jsonPath("$.closed").value(expected.closed()));
    }

    private void assertOk(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk());
    }

    private static void assertContentTypeJson(ResultActions resultActions) throws Exception {
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private static TasksSummary tasksSummary() {
        return TasksSummary.builder()
                .total(100)
                .open(80)
                .overdue(5)
                .closed(20)
                .build();
    }

    private static Page<DashboardTaskPreview> dashboardTaskPreviewPage(int pageNo, int pageSize) {
        final var total = new Random().nextInt(pageSize, 10000);
        final var totalPages = total / pageSize;
        final var content = IntStream.range(0, pageSize)
                .mapToObj((index) -> dashboardTaskPreview())
                .toList();
        return Page.<DashboardTaskPreview>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .total(total)
                .content(content)
                .build();
    }

    private static DashboardTaskPreview dashboardTaskPreview() {
        long random = new Random().nextLong(1, 999);
        return DashboardTaskPreview.builder()
                .createdAt(Instant.now())
                .taskId(new TaskId(random))
                .number(new TaskNumber(random))
                .title("task %d".formatted(random))
                .projectId(new ProjectId(random))
                .projectTitle("Project %d".formatted(random))
                .dueDate(LocalDate.now().minusDays(1))
                .isOverdue(true)
                .priority(TaskPriority.MEDIUM)
                .status("In progress")
                .assignee(UserInfo.builder()
                        .id(new UserId(DEFAULT_USER_ID_VALUE))
                        .email(new Email("user@domain.com"))
                        .firstName("FName")
                        .lastName("LName")
                        .build())
                .build();
    }
}