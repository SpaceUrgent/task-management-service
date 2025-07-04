package com.task.management.application.project.handler;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.port.out.UserInfoRepositoryPort;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.project.event.TaskReassignedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

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
        if (source == null) return null;
        return userInfoRepositoryPort.find(source)
                .map(UserInfo::fullName)
                .orElseThrow(() -> new EventHandlingException("User with id value %s not found".formatted(source.value())));
    }
}
