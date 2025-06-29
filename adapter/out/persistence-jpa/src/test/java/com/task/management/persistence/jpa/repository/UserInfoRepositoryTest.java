package com.task.management.persistence.jpa.repository;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
class UserInfoRepositoryTest {
    @Autowired
    private JpaUserInfoRepositoryAdapter userInfoRepository;
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;

    @Test
    void findById_shouldReturnOptionalOfProjectUser_whenUserExists() {
        final var expected = getFirstJpaUser();
        final var givenId = new UserId(expected.getId());
        final var actual = userInfoRepository.find(givenId).orElse(null);
        assertNotNull(actual);
        assertMatches(expected, actual);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenId = new UserId(randomLong());
        assertTrue(userInfoRepository.find(givenId).isEmpty());
    }

    @Test
    void findByEmail_shouldReturnOptionalOfProjectUser_whenUserExists() {
        final var expected = getFirstJpaUser();
        final var givenEmail = new Email(expected.getEmail());
        final var actual = userInfoRepository.find(givenEmail).orElse(null);
        assertNotNull(actual);
        assertMatches(expected, actual);
    }

    @Test
    void findByEmail_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenEmail = new Email("non-existing@mail.com");
        assertTrue(userInfoRepository.find(givenEmail).isEmpty());
    }

    private UserEntity getFirstJpaUser() {
        return userEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 user is expected for test"));
    }

    private static void assertMatches(UserEntity expected, UserInfo actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getEmail(), actual.email().value());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }

    private static long randomLong() {
        return new Random().nextLong();
    }
}