package com.task.management.application.service.mapper;

public final class Mappers {
    private static final ProjectUserMapper projectUserMapper;
    private static final TaskMapper taskMapper;
    private static final TaskDetailsMapper taskDetailsMapper;

    static {
        projectUserMapper = new ProjectUserMapper();
        taskMapper = new TaskMapper(projectUserMapper);
        taskDetailsMapper = new TaskDetailsMapper(projectUserMapper);
    }

    public static TaskMapper taskMapper() {
        return Mappers.taskMapper;
    }

    public static TaskDetailsMapper taskDetailsMapper() {
        return Mappers.taskDetailsMapper;
    }

    private Mappers() {
    }
}
