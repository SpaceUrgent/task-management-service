package com.task.management.domain.project.application.handler;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.event.EventHandlingException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.common.port.out.UserInfoRepositoryPort;
import com.task.management.domain.project.event.TaskReassignedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.port.out.TaskRepositoryPort;

@AppComponent
public class TaskReassignedEventHandler extends TaskUpdatedEventHandler<TaskReassignedEvent, UserId> {
    private final UserInfoRepositoryPort userInfoRepositoryPort;

    protected TaskReassignedEventHandler(TaskRepositoryPort taskRepositoryPort,
                                         UserInfoRepositoryPort userInfoRepositoryPort) {
        super(taskRepositoryPort);
        this.userInfoRepositoryPort = userInfoRepositoryPort;
    }

    @Override
    public Class<TaskReassignedEvent> eventType() {
        return TaskReassignedEvent.class;
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.ASSIGNEE;
    }

    @Override
    protected String mapToString(UserId source) throws EventHandlingException {
        return userInfoRepositoryPort.find(source)
                .map(UserInfo::fullName)
                .orElseThrow(() -> new EventHandlingException("User with id value %s not found".formatted(source.value())));
    }
}
