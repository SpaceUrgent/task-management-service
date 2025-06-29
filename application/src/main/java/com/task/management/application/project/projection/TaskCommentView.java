package com.task.management.application.project.projection;

import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.project.model.objectvalue.TaskCommentId;
import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TaskCommentView(
        TaskCommentId id,
        Instant createdAt,
        UserInfo author,
        String content
) {

    @Builder
    public TaskCommentView {
        parameterRequired(id, "Task comment id");
        parameterRequired(createdAt, "Created at");
        parameterRequired(author, "Author");
        parameterRequired(content, "Content");
    }
}
