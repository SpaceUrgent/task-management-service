package com.task.management.application.dashboard.query;

import com.task.management.application.common.query.PagedQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindAssignedDashboardTasksQuery extends PagedQuery {
    private final UserId assignee;
    private final boolean overdueOnly;

    protected FindAssignedDashboardTasksQuery(Builder builder) {
        super(builder);
        this.assignee = parameterRequired(builder.assignee, "Assignee");
        this.overdueOnly = builder.overdueOnly;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends PagedQuery.PagedQueryBuilder<Builder, FindAssignedDashboardTasksQuery> {
        private UserId assignee;
        private boolean overdueOnly;

        @Override
        public FindAssignedDashboardTasksQuery build() {
            return new FindAssignedDashboardTasksQuery(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder assignee(UserId assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder overdueOnly() {
            this.overdueOnly = true;
            return this;
        }
    }
}
