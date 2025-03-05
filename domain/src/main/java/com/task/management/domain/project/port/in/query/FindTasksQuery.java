package com.task.management.domain.project.port.in.query;

import com.task.management.domain.common.PagedQuery;
import com.task.management.domain.common.Sort;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindTasksQuery extends PagedQuery {
    private final ProjectId projectId;
    private final Set<TaskStatus> statuses;
    private final ProjectUserId assigneeId;

    private FindTasksQuery(Builder builder) {
        super(builder);
        this.projectId = projectIdRequired(builder.projectId);
        this.statuses = builder.statuses;
        this.assigneeId = builder.assigneeId;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static ProjectId projectIdRequired(ProjectId projectId) {
        return requireNonNull(projectId, "Project id is required");
    }

    public static class Builder extends PagedQuery.PagedQueryBuilder<Builder, FindTasksQuery> {
        private ProjectId projectId;
        private Set<TaskStatus> statuses;
        private ProjectUserId assigneeId;

        @Override
        public FindTasksQuery build() {
            return new FindTasksQuery(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder projectId(ProjectId projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder statusIn(Set<TaskStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder assigneeId(ProjectUserId assigneeId) {
            this.assigneeId = assigneeId;
            return this;
        }

        public Builder sortByCreatedAt(Sort.Direction direction) {
            this.sortBy.add(Sort.by("createdAt", direction));
            return this;
        }
    }
}
