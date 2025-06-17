package com.task.management.application.dashboard.service;

import com.task.management.application.common.projection.Page;
import com.task.management.application.dashboard.port.out.TasksDashboardRepository;
import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.projection.TasksSummary;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.shared.model.objectvalue.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.IntStream;

import static com.task.management.application.common.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TasksDashboardServiceTest {
    @Mock
    private TasksDashboardRepository tasksDashboardRepository;
    @InjectMocks
    private TasksDashboardService tasksDashboardService;

    @Test
    void getAssignedToSummary_shouldReturnSummary_whenAllConditionsMet() {
        final var givenActor = randomUserId();
        final var tasksSummary = getTasksSummary();
        doReturn(tasksSummary).when(tasksDashboardRepository).getAssignedToSummary(eq(givenActor));
        assertEquals(tasksSummary, tasksDashboardService.getAssignedToSummary(givenActor));
    }

    @Test
    void getOwnedBySummary_shouldReturnSummary_whenAllConditionsMet() {
        final var givenActor = randomUserId();
        final var tasksSummary = getTasksSummary();
        doReturn(tasksSummary).when(tasksDashboardRepository).getOwnedBySummary(eq(givenActor));
        assertEquals(tasksSummary, tasksDashboardService.getOwnedBySummary(givenActor));
    }

    @Test
    void getAssignedTo_shouldReturnPage_whenAllConditionsMet() {
        final var givenActor = randomUserId();
        final var givenQuery = FindAssignedDashboardTasksQuery.builder()
                .pageNumber(1)
                .pageSize(10)
                .assignee(givenActor)
                .build();
        final var expected = createDashboardTaskPreviewPage(givenActor, givenQuery.getPageNumber(), givenQuery.getPageSize());
        doReturn(expected).when(tasksDashboardRepository).getAssignedTo(eq(givenQuery));
        assertEquals(expected, tasksDashboardService.getAssignedTo(givenQuery));
    }


    @Test
    void getOwnedBy_shouldReturnPage_whenAllConditionsMet() {
        final var givenActor = randomUserId();
        final var givenQuery = FindOwnedDashboardTasksQuery.builder()
                .pageNumber(1)
                .pageSize(10)
                .owner(givenActor)
                .build();
        final var expected = createDashboardTaskPreviewPage(randomUserId(), givenQuery.getPageNumber(), givenQuery.getPageSize());
        doReturn(expected).when(tasksDashboardRepository).getOwnedBy(eq(givenQuery));
        assertEquals(expected, tasksDashboardService.getOwnedBy(givenQuery));
    }

    private static TasksSummary getTasksSummary() {
        return TasksSummary.builder()
                .total(100)
                .open(20)
                .overdue(1)
                .closed(80)
                .build();
    }

    private Page<DashboardTaskPreview> createDashboardTaskPreviewPage(UserId givenActor,
                                                                      Integer pageNumber,
                                                                      Integer pageSize) {
        final var previews = IntStream.range(0, pageSize)
                .mapToObj(index -> createDashboardTaskPreview(givenActor))
                .toList();
        return Page.<DashboardTaskPreview>builder()
                .totalPages(pageNumber)
                .total(pageNumber * pageSize)
                .pageNo(pageNumber)
                .pageSize(pageSize)
                .content(previews)
                .build();
    }

    private static DashboardTaskPreview createDashboardTaskPreview(UserId givenActor) {
        var taskId = randomTaskId();
        var projectId = randomProjectId();
        return DashboardTaskPreview.builder()
                .createdAt(Instant.now())
                .taskId(taskId)
                .title("Task %d".formatted(taskId.value()))
                .number(new TaskNumber(taskId.value()))
                .projectId(projectId)
                .projectTitle("Project %d".formatted(projectId.value()))
                .dueDate(LocalDate.now())
                .isOverdue(false)
                .priority(TaskPriority.MEDIUM)
                .status("In progress")
                .assignee(UserInfo.builder()
                        .id(givenActor)
                        .email(new Email("username@domain.com"))
                        .firstName("FName")
                        .lastName("LName")
                        .build())
                .build();
    }
}