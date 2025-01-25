package com.task.management.persistence.jpa;

import com.task.management.application.model.Project;
import com.task.management.application.model.User;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaProjectRepositoryAdapterTest {
    private static final String PROJECT_TITLE = "Project title";
    private static final String PROJECT_DESCRIPTION = "Project description";

    @Autowired
    private JpaUserRepositoryAdapter userRepository;
    @Autowired
    private JpaProjectRepositoryAdapter projectRepository;
    @Autowired
    private JpaProjectRepository jpaProjectRepository;

    @Test
    void add_shouldReturnSavedProject() {
        var owner = userRepository.add(getTestUser());
        final var givenProject = getTestProject(owner);
        final var added = projectRepository.add(givenProject);
        assertMatches(givenProject, added);
        final var savedJpaUser = jpaProjectRepository.findById(added.getId().value()).orElseThrow();
        assertMatches(added, savedJpaUser);
        assertNotNull(savedJpaUser.getCreatedAt());
    }

    private void assertMatches(Project expected, ProjectEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().value(), actual.getOwner().getId());
    }

    private void assertMatches(Project expected, Project actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner(), actual.getOwner());
    }

    private static Project getTestProject(User owner) {
        return Project.builder()
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .owner(owner.getId())
                .build();
    }

    private static User getTestUser() {
        return User.builder()
                .email("test@domain.com")
                .firstName("John")
                .lastName("Doe")
                .encryptedPassword("encryptedPassword")
                .build();
    }

}