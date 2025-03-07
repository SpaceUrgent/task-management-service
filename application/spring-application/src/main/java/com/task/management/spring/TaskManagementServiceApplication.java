package com.task.management.spring;

import com.task.managment.web.controller.AuthController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {
//        "com.task.managment.web"
//})
@SpringBootApplication
public class TaskManagementServiceApplication {


    public static void main(String[] args) {
        final var context = SpringApplication.run(TaskManagementServiceApplication.class, args);
        AuthController bean = context.getBean(AuthController.class);
    }
}
