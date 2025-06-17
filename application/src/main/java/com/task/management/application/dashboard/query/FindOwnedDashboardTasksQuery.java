package com.task.management.application.dashboard.query;

import com.task.management.application.shared.query.PagedQuery;
import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindOwnedDashboardTasksQuery extends PagedQuery {
    private final UserId owner;
    private final boolean overdueOnly;
    
    protected FindOwnedDashboardTasksQuery(Builder builder) {
        super(builder);
        this.owner = parameterRequired(builder.owner, "Owner");
        this.overdueOnly = builder.overdueOnly;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends PagedQueryBuilder<Builder, FindOwnedDashboardTasksQuery> {
        private UserId owner;
        private boolean overdueOnly;
        
        @Override
        public FindOwnedDashboardTasksQuery build() {
            return new FindOwnedDashboardTasksQuery(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
        
        public Builder owner(UserId owner) {
            this.owner = owner;
            return this;
        }

        public Builder overdueOnly() {
            this.overdueOnly = true;
            return this;
        }
    }
}
