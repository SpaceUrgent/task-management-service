package com.task.management.persistence.jpa.project;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {
                "classpath:sql/insert_users.sql",
                "classpath:sql/insert_user_with_projects.sql"
        }
)
@PersistenceTest
class JpaProjectUserRepositoryAdapterTest {
    @Autowired
    private JpaProjectUserRepositoryAdapter projectUserRepositoryAdapter;
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;

    @Test
    void findById_shouldReturnOptionalOfProjectUser_whenUserExists() {
        final var expected = getFirstJpaUser();
        final var givenId = new ProjectUserId(expected.getId());
        final var actual = projectUserRepositoryAdapter.find(givenId).orElse(null);
        assertNotNull(actual);
        assertMatches(expected, actual);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenId = new ProjectUserId(randomLong());
        assertTrue(projectUserRepositoryAdapter.find(givenId).isEmpty());
    }

    @Test
    void findByEmail_shouldReturnOptionalOfProjectUser_whenUserExists() {
        final var expected = getFirstJpaUser();
        final var givenEmail = new Email(expected.getEmail());
        final var actual = projectUserRepositoryAdapter.find(givenEmail).orElse(null);
        assertNotNull(actual);
        assertMatches(expected, actual);
    }

    @Test
    void findByEmail_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenEmail = new Email("non-existing@mail.com");
        assertTrue(projectUserRepositoryAdapter.find(givenEmail).isEmpty());
    }

    private UserEntity getFirstJpaUser() {
        return userEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 user is expected for test"));
    }

    private static void assertMatches(UserEntity expected, ProjectUser actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email().value());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }

    private static long randomLong() {
        return new Random().nextLong();
    }
}