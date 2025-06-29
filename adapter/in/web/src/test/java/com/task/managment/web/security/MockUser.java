package com.task.managment.web.security;

import com.task.managment.web.TestUtils;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUserSecurityContextFactory.class)
public @interface MockUser {
    long DEFAULT_USER_ID_VALUE = 1L;

    long id() default DEFAULT_USER_ID_VALUE;

    String email() default "username@domain.com";

    String firstName() default "John";

    String lastName() default "Doe";

    String password() default "password";
}
