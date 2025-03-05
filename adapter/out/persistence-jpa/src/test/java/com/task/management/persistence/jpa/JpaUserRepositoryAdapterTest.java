package com.task.management.persistence.jpa;

import com.task.management.domain.iam.model.User;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:sql/insert_users.sql"
)
@Transactional
@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaUserRepositoryAdapterTest {

    @Autowired
    private JpaUserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private UserEntityDao userEntityDao;

    @Test
    void save_shouldReturnSaved_whenNewUserSaved() {
        var givenUser = User.builder()
                .createdAt(Instant.now())
                .email("new-user@mail.com")
                .firstName("Sam")
                .lastName("Serious")
                .encryptedPassword("encryptedPassword")
                .build();
        final var saved = userRepositoryAdapter.add(givenUser);
        assertMatches(givenUser, saved);
        assertMatches(saved, userEntityDao.findById(saved.getId().value()).orElseThrow());
    }

    @Test
    void save_shouldReturnUpdated_whenExistedUserWasSaved() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenUser = User.builder()
                .id(new UserId(existingUserEntity.getId()))
                .createdAt(Instant.now())
                .email("updated-email@mail.com")
                .firstName(existingUserEntity.getFirstName())
                .lastName(existingUserEntity.getLastName())
                .encryptedPassword(existingUserEntity.getEncryptedPassword())
                .build();
        final var saved = userRepositoryAdapter.add(givenUser);
        assertEquals(givenUser, saved);
        assertMatches(saved, userEntityDao.findById(saved.getId().value()).orElseThrow());
    }

    @Test
    void findUserProfile_shouldReturnOptionalOfUserProfile_whenUserExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenUserId = new UserId(existingUserEntity.getId());
        final var result = userRepositoryAdapter.find(givenUserId);
        assertTrue(result.isPresent());
        assertMatches(existingUserEntity, result.get());
    }

    @Test
    void findUserProfile_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenUserId = new UserId(new Random().nextLong());
        assertTrue(userRepositoryAdapter.find(givenUserId).isEmpty());
    }

    @Test
    void emailExists_shouldReturnTrue_whenUserWithGivenEmailExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenEmail = existingUserEntity.getEmail();
        assertTrue(userRepositoryAdapter.emailExists(givenEmail));
    }

    @Test
    void emailExists_shouldReturnFalse_whenUserWithGivenEmailDoesNotExists() {
        final var givenEmail = "non-existing@mail.com";
        assertFalse(userRepositoryAdapter.emailExists(givenEmail));
    }

    private static void assertMatches(User expected, UserEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEncryptedPassword(), actual.getEncryptedPassword());
    }

    private static void assertMatches(User expected, User actual) {
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEncryptedPassword(), actual.getEncryptedPassword());
    }


    private void assertMatches(UserEntity expected, UserProfile actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }
}