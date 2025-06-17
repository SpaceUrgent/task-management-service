package com.task.management.persistence.jpa.iam;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.iam.model.User;
import com.task.management.application.iam.projection.UserCredentials;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import com.task.management.persistence.jpa.repository.JpaUserRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

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
@PersistenceTest
class JpaUserRepositoryAdapterTest {

    @Autowired
    private JpaUserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private UserEntityDao userEntityDao;

    @Test
    void save_shouldReturnSaved_whenNewUserSaved() {
        var givenUser = User.builder()
                .createdAt(Instant.now())
                .email(new Email("new-user@mail.com"))
                .firstName("Sam")
                .lastName("Serious")
                .encryptedPassword("encryptedPassword")
                .build();
        final var saved = userRepositoryAdapter.save(givenUser);
        assertMatches(givenUser, saved);
        assertMatches(saved, userEntityDao.findById(saved.getId().value()).orElseThrow());
    }

    @Test
    void save_shouldReturnUpdated_whenExistedUserWasSaved() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenUser = User.builder()
                .id(new UserId(existingUserEntity.getId()))
                .createdAt(Instant.now())
                .email(new Email("updated-email@mail.com"))
                .firstName(existingUserEntity.getFirstName())
                .lastName(existingUserEntity.getLastName())
                .encryptedPassword(existingUserEntity.getEncryptedPassword())
                .build();
        final var saved = userRepositoryAdapter.save(givenUser);
        assertEquals(givenUser, saved);
        assertMatches(saved, userEntityDao.findById(saved.getId().value()).orElseThrow());
    }

    @Test
    void find_shouldReturnOptionalOfUser_whenUserExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenUserId = new UserId(existingUserEntity.getId());
        final var result = userRepositoryAdapter.find(givenUserId);
        assertTrue(result.isPresent());
        assertMatches(existingUserEntity, result.get());
    }

    @Test
    void find_shouldReturnOptionalEmpty_whenUserDoesNotExist() {
        final var givenUserId = new UserId(new Random().nextLong());
        assertTrue(userRepositoryAdapter.find(givenUserId).isEmpty());
    }

    @Test
    void findUserInfo_shouldReturnOptionalOfUserInfo_whenUserExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenUserId = new UserId(existingUserEntity.getId());
        final var result = userRepositoryAdapter.findUserInfo(givenUserId);
        assertTrue(result.isPresent());
        assertMatches(existingUserEntity, result.get());
    }

    @Test
    void findUserInfo_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenUserId = new UserId(new Random().nextLong());
        assertTrue(userRepositoryAdapter.findUserInfo(givenUserId).isEmpty());
    }

    @Test
    void emailExists_shouldReturnTrue_whenUserWithGivenEmailExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var givenEmail = new Email(existingUserEntity.getEmail());
        assertTrue(userRepositoryAdapter.emailExists(givenEmail));
    }

    @Test
    void emailExists_shouldReturnFalse_whenUserWithGivenEmailDoesNotExists() {
        final var givenEmail = new Email("non-existing@mail.com");
        assertFalse(userRepositoryAdapter.emailExists(givenEmail));
    }

    @Test
    void findCredentialsByEmail_shouldReturnOptionalOfCredentials_whenUserExists() {
        final var existingUserEntity = userEntityDao.findAll().stream().findFirst().orElseThrow();
        final var result = userRepositoryAdapter.findByEmail(new Email(existingUserEntity.getEmail()));
        assertTrue(result.isPresent());
        assertMatches(existingUserEntity, result.get());
    }

    @Test
    void findCredentialsByEmail_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenEmail = new Email("non-existing@mail.com");
        assertTrue(userRepositoryAdapter.findByEmail(givenEmail).isEmpty());
    }

    private void assertMatches(UserEntity expected, UserCredentials actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email().value());
        assertEquals(expected.getEncryptedPassword(), actual.encryptedPassword());
    }

    private static void assertMatches(User expected, UserEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getEmail().value(), actual.getEmail());
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


    private void assertMatches(UserEntity expected, UserInfo actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email().value());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }

    private void assertMatches(UserEntity expected, User actual) {
        assertEquals(expected.getId(), actual.getId().value());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        assertEquals(expected.getEmail(), actual.getEmail().value());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEncryptedPassword(), actual.getEncryptedPassword());
    }
}