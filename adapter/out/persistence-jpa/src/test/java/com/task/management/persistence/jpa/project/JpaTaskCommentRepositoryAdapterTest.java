package com.task.management.persistence.jpa.project;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.TaskComment;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.TaskCommentEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.entity.TaskCommentEntity;
import com.task.management.persistence.jpa.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@PersistenceTest
class JpaTaskCommentRepositoryAdapterTest {
    @Autowired
    private TaskEntityDao taskEntityDao;
    @Autowired
    private TaskCommentEntityDao taskCommentEntityDao;
    @Autowired
    private JpaTaskCommentRepositoryAdapter jpaTaskCommentRepositoryAdapter;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void save() {
        final var taskEntity = getFirstTaskEntity();
        final var givenTaskComment = TaskComment.builder()
                .createdAt(Instant.now())
                .task(new TaskId(taskEntity.getId()))
                .author(new UserId(taskEntity.getAssignee().getId()))
                .content("Help me!")
                .build();
        final var saved = jpaTaskCommentRepositoryAdapter.save(givenTaskComment);
        assertMatches(givenTaskComment, saved);
        final var savedEntity = taskCommentEntityDao.findById(saved.getId().value()).orElseThrow();
        assertMatches(saved, savedEntity);
    }

    private TaskEntity getFirstTaskEntity() {
        return taskEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 task is expected in DB for test"));
    }

    private static void assertMatches(TaskComment expected, TaskComment actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTask(), actual.getTask());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getContent(), actual.getContent());
    }

    private void assertMatches(TaskComment expected, TaskCommentEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTask().value(), actual.getTask().getId());
        assertEquals(expected.getAuthor().value(), actual.getAuthor().getId());
        assertEquals(expected.getContent(), actual.getContent());
    }
}