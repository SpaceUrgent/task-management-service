package com.task.management.persistence.jpa;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.persistence.jpa.entity.UserEntity;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaUserRepositoryAdapterTest {

    @Autowired
    private JpaUserRepositoryAdapter userRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    void findById_shouldReturnOptionalOfUser_whenUserExists() {
        final var expectedUser = userRepository.add(getTestUser());
        final var userOptional = userRepository.findById(expectedUser.getId());
        assertTrue(userOptional.isPresent());
        assertEquals(expectedUser, userOptional.get());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserDoesNotExist() {
        final var givenId = randomUserId();
        assertTrue(userRepository.findById(givenId).isEmpty());
    }

    @Test
    void add_shouldSaveNewUser() {
        final var user = getTestUser();
        final var added = userRepository.add(user);
        assertNotNull(added.getId());
        assertMatches(user, added);
        assertMatches(added, jpaUserRepository.findById(added.getId().value()).orElseThrow());
    }

    @Test
    void emailExists_shouldReturnTrue_whenGivenEmailExists() {
        final var user = getTestUser();
        userRepository.add(user);
        assertTrue(userRepository.emailExists(user.getEmail()));
    }

    @Test
    void emailExists_shouldReturnFalse_whenGivenEmailDoesNotExist() {

        assertFalse(userRepository.emailExists("non-existing@domain.com"));
    }

    private static User getTestUser() {
        return User.builder()
                .email("test@domain.com")
                .firstName("John")
                .lastName("Doe")
                .encryptedPassword("encryptedPassword")
                .build();
    }

    private static UserId randomUserId() {
        return new UserId(new Random().nextLong());
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
}