package com.task.management.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAspectJAutoProxy
@SpringBootApplication
public class TaskManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementServiceApplication.class, args);
    }
}
