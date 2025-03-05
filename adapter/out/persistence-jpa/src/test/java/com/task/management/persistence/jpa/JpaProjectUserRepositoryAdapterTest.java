package com.task.management.persistence.jpa;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

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
@Transactional
@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaProjectUserRepositoryAdapterTest {
    @Autowired
    private JpaProjectUserRepositoryAdapter projectUserRepositoryAdapter;
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;

    @Test
    void findMember_shouldReturnOptionalOfMember_whenMemberExists() {
        final var existingUser = userEntityDao.findAll().stream()
                .filter(userWithProjects())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least onw user with project expected for test"));
        final var givenUserId = new ProjectUserId(existingUser.getId());
        final var givenProjectId = existingUser.getProjects().stream()
                .map(ProjectEntity::getId)
                .map(ProjectId::new)
                .findFirst()
                .orElseThrow();
        final var result = projectUserRepositoryAdapter.findMember(givenUserId, givenProjectId).orElse(null);
        assertNotNull(result);
        assertMatches(existingUser, result);
    }

    @Test
    void findMember_shouldReturnEmptyOptional_whenMemberDoesNotExist() {
        final var givenUserId = new ProjectUserId(randomLong());
        final var givenProjectId = new ProjectId(randomLong());
        assertTrue(projectUserRepositoryAdapter.findMember(givenUserId, givenProjectId).isEmpty());
    }

    @Test
    void findMembers_shouldReturnListOfMembers() {
        final var project = projectEntityDao.findAll().stream()
                .max(Comparator.comparing(projectEntity -> projectEntity.getMembers().size()))
                .orElseThrow(() -> new IllegalStateException("Project with members expected"));
        final var givenProjectId = new ProjectId(project.getId());
        final var expected = project.getMembers();
        final var result = projectUserRepositoryAdapter.findMembers(givenProjectId);
        assertMatches(expected, result);
    }

    @Test
    void findById_shouldReturnOptionalOfProjectUser_whenUserExists() {
        final var expected = getJpaUser();
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
        final var expected = getJpaUser();
        final var givenEmail = expected.getEmail();
        final var actual = projectUserRepositoryAdapter.find(givenEmail).orElse(null);
        assertNotNull(actual);
        assertMatches(expected, actual);
    }

    @Test
    void findByEmail_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenEmail = "non-existing@mail.com";
        assertTrue(projectUserRepositoryAdapter.find(givenEmail).isEmpty());
    }

    private UserEntity getJpaUser() {
        return userEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 user is expected for test"));
    }

    private void assertMatches(List<UserEntity> expected, List<ProjectUser> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(UserEntity expected, ProjectUser actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }

    private static long randomLong() {
        return new Random().nextLong();
    }

    private static Predicate<UserEntity> userWithProjects() {
        return userEntity -> !userEntity.getProjects().isEmpty();
    }
}