package com.task.management.application.project.query;

import com.task.management.application.shared.query.PagedQuery;
import com.task.management.application.shared.query.Sort;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindTasksQuery extends PagedQuery {
    private final ProjectId projectId;
    private final Set<String> statuses;
    @Deprecated
    private final UserId assigneeId;
    private final Set<UserId> assignees;
    private final Boolean includeUnassigned;

    private FindTasksQuery(Builder builder) {
        super(builder);
        this.projectId = projectIdRequired(builder.projectId);
        this.statuses = builder.statuses;
        this.assigneeId = builder.assigneeId;
        this.assignees = builder.assignees;
        this.includeUnassigned = builder.includeUnassigned;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static ProjectId projectIdRequired(ProjectId projectId) {
        return parameterRequired(projectId, "Project id");
    }

    public static class Builder extends PagedQuery.PagedQueryBuilder<Builder, FindTasksQuery> {
        private ProjectId projectId;
        private Set<String> statuses;
        @Deprecated
        private UserId assigneeId;
        private Set<UserId> assignees;
        private Boolean includeUnassigned;

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

        public Builder statusIn(Set<String> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder assigneeId(UserId assigneeId) {
            this.assigneeId = assigneeId;
            return this;
        }

        public Builder assignees(Set<UserId> assignees) {
            this.assignees = assignees;
            return this;
        }

        public Builder includeUnassigned(Boolean value) {
            this.includeUnassigned = value;
            return this;
        }

        public Builder sortByCreatedAt(Sort.Direction direction) {
            this.sortBy.add(Sort.by("createdAt", direction));
            return this;
        }
    }
}
