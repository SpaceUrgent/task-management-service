package com.task.managment.web;

import com.task.managment.web.controller.AuthController;
import com.task.managment.web.controller.GlobalExceptionHandler;
import com.task.managment.web.controller.ProjectController;
import com.task.managment.web.controller.UserController;
import com.task.managment.web.security.SecurityConfiguration;
import com.task.managment.web.security.UserDetailServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(
        classes = {
                WebTestConfiguration.class,
                SecurityConfiguration.class,
                UserDetailServiceImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@SpringJUnitConfig
@Import({})
public @interface WebTest {

        @AliasFor(annotation = Import.class, attribute = "value")
        Class<?> controllerClass();
}
