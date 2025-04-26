package com.task.management.domain.project.application.query;

import com.task.management.domain.common.application.query.PagedQuery;
import com.task.management.domain.common.application.query.Sort;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.TaskStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindTasksQuery extends PagedQuery {
    private final ProjectId projectId;
    private final Set<TaskStatus> statuses;
    private final UserId assigneeId;

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
        return parameterRequired(projectId, "Project id");
    }

    public static class Builder extends PagedQuery.PagedQueryBuilder<Builder, FindTasksQuery> {
        private ProjectId projectId;
        private Set<TaskStatus> statuses;
        private UserId assigneeId;

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

        public Builder assigneeId(UserId assigneeId) {
            this.assigneeId = assigneeId;
            return this;
        }

        public Builder sortByCreatedAt(Sort.Direction direction) {
            this.sortBy.add(Sort.by("createdAt", direction));
            return this;
        }
    }
}
