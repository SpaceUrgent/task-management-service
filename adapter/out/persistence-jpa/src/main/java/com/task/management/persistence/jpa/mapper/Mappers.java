package com.task.management.persistence.jpa.mapper;

public final class Mappers {
    public static final UserMapper userMapper = new UserMapper();
    public static final UserProfileMapper userProfileMapper = new UserProfileMapper();
    public static final ProjectUserMapper projectUserMapper = new ProjectUserMapper();
    public static final ProjectMapper projectMapper = new ProjectMapper(projectUserMapper);
    public static final ProjectPreviewMapper projectPreviewMapper = new ProjectPreviewMapper(projectUserMapper);
    public static final TaskMapper taskMapper = new TaskMapper(projectUserMapper);
    public static final TaskPreviewMapper taskPreviewMapper = new TaskPreviewMapper(projectUserMapper);
    public static final TaskDetailsMapper taskDetailsMapper = new TaskDetailsMapper(projectUserMapper);
    public static final UserCredentialsMapper userCredentialsMapper = new UserCredentialsMapper();
}
