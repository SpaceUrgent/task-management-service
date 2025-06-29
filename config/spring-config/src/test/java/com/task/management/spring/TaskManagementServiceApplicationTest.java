package com.task.management.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfigurations.class})
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=update"})
class TaskManagementServiceApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TestUseCaseService testUseCaseService;

    @Test
    void transactionalUseCaseAspectTest() {
        assertTrue(testUseCaseService.isActiveTransaction());
    }
}