package com.task.management.persistence.jpa.dashboard;

import com.task.management.application.dashboard.projection.DashboardTaskPreview;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.application.dashboard.query.FindOwnedDashboardTasksQuery;
import com.task.management.domain.shared.model.objectvalue.*;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.repository.JpaTasksDashboardRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@PersistenceTest
class JpaTasksDashboardRepositoryAdapterTest {
    @Autowired
    private TaskEntityDao taskEntityDao;
    @Autowired
    private JpaTasksDashboardRepositoryAdapter tasksDashboardRepository;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getAssignedToSummary() {
        final var taskEntities = taskEntityDao.findAll();
        final var filteredByAssigneeTaskEntities = taskEntities.stream()
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getAssignee().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with assignee not found"))
                .getValue();
        final var openTasks = filteredByAssigneeTaskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .count();
        final var closedTasks = filteredByAssigneeTaskEntities.size() - openTasks;
        final var overdueTasks = filteredByAssigneeTaskEntities.stream()
                .filter(entity -> nonNull(entity.getDueDate()))
                .filter(entity -> entity.getDueDate().isBefore(LocalDate.now()))
                .count();
        final var summary = tasksDashboardRepository.getAssignedToSummary(new UserId(filteredByAssigneeTaskEntities.getFirst().getAssignee().getId()));
        assertEquals(filteredByAssigneeTaskEntities.size(), summary.total());
        assertEquals(openTasks, summary.open().longValue());
        assertEquals(overdueTasks, summary.overdue().longValue());
        assertEquals(closedTasks, summary.closed().longValue());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getOwnedBySummary() {
        final var taskEntities = taskEntityDao.findAll();
        final var filteredByOwnerTaskEntities = taskEntities.stream()
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getOwner().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with owner not found"))
                .getValue();
        final var openTasks = filteredByOwnerTaskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .count();
        final var closedTasks = filteredByOwnerTaskEntities.size() - openTasks;
        final var overdueTasks = filteredByOwnerTaskEntities.stream()
                .filter(entity -> nonNull(entity.getDueDate()))
                .filter(entity -> entity.getDueDate().isBefore(LocalDate.now()))
                .count();
        final var summary = tasksDashboardRepository.getOwnedBySummary(new UserId(filteredByOwnerTaskEntities.getFirst().getOwner().getId()));
        assertEquals(filteredByOwnerTaskEntities.size(), summary.total());
        assertEquals(openTasks, summary.open().longValue());
        assertEquals(overdueTasks, summary.overdue().longValue());
        assertEquals(closedTasks, summary.closed().longValue());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getAssignedTo() {
        final var taskEntities = taskEntityDao.findAll();
        final var expectedTaskEntities = taskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getAssignee().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with assignee not found"))
                .getValue();


        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;

        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindAssignedDashboardTasksQuery.builder()
                    .assignee(new UserId(expectedTaskEntities.getFirst().getAssignee().getId()))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = tasksDashboardRepository.getAssignedTo(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(expectedTaskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getAssignedTo_overdueOnly() {
        final var taskEntities = taskEntityDao.findAll();
        final var expectedTaskEntities = taskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .filter(entity -> nonNull(entity.getDueDate()))
                .filter(entity -> entity.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getAssignee().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with assignee not found"))
                .getValue();


        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;

        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindAssignedDashboardTasksQuery.builder()
                    .assignee(new UserId(expectedTaskEntities.getFirst().getAssignee().getId()))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = tasksDashboardRepository.getAssignedTo(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(expectedTaskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getOwnedBy() {
        final var taskEntities = taskEntityDao.findAll();
        final var expectedTaskEntities = taskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getOwner().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with assignee not found"))
                .getValue();


        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;

        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindOwnedDashboardTasksQuery.builder()
                    .owner(new UserId(expectedTaskEntities.getFirst().getOwner().getId()))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = tasksDashboardRepository.getOwnedBy(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(Math.ceilDivExact(expectedTaskEntities.size(), givenQuery.getPageSize()), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void getOwnedBy_overdueOnly() {
        final var taskEntities = taskEntityDao.findAll();
        final var expectedTaskEntities = taskEntities.stream()
                .filter(entity -> !entity.getStatus().isFinal())
                .filter(entity -> nonNull(entity.getDueDate()))
                .filter(entity -> entity.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.groupingBy(
                        taskEntity -> taskEntity.getOwner().getId(),
                        Collectors.toList()))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new InvalidTestSetupException("Tasks with assignee not found"))
                .getValue();


        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;

        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindOwnedDashboardTasksQuery.builder()
                    .owner(new UserId(expectedTaskEntities.getFirst().getOwner().getId()))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = tasksDashboardRepository.getOwnedBy(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(expectedTaskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    private void assertMatches(List<TaskEntity> expected, List<DashboardTaskPreview> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size()).forEach(index -> {
            assertMatches(expected.get(index), actual.get(index));
        });
    }

    private void assertMatches(TaskEntity expected, DashboardTaskPreview actual) {
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getId(), actual.taskId().value());
        assertEquals(expected.getNumber(), actual.number().value());
        assertEquals(expected.getTitle(), actual.title());
        final var projectEntity = expected.getProject();
        assertEquals(projectEntity.getId(), actual.projectId().value());
        assertEquals(projectEntity.getTitle(), actual.projectTitle());
        final var expectedDueDate = expected.getDueDate();
        assertEquals(expectedDueDate, actual.dueDate());
        assertEquals(nonNull(expectedDueDate) && expectedDueDate.isBefore(LocalDate.now()), actual.isOverdue());
        assertEquals(expected.getPriority(), actual.priority().order());
        assertFalse(expected.getStatus().isFinal());
        assertEquals(expected.getStatusName(), actual.status());
        assertEquals(expected.getAssignee().getId(), actual.assignee().id().value());
    }

    private static <T> List<T> slice (List<T> target, int page, int size) {
        return IntStream.range(page * size, (page * size) + size)
                .mapToObj(target::get)
                .toList();
    }
}