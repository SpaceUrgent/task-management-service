package com.task.management.application.shared.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Data
public abstract class PagedQuery {
    private final Integer pageNumber;
    private final Integer pageSize;
    private final List<Sort> sortBy = new ArrayList<>();

    protected PagedQuery(PagedQueryBuilder<?, ?> builder) {
        this.pageNumber = requireNonNull(builder.pageNumber, "Page number is required");
        this.pageSize = requireNonNull(builder.pageSize, "Page size is required");
        this.sortBy.addAll(builder.sortBy);
    }

    protected static abstract class PagedQueryBuilder<Builder extends PagedQueryBuilder<Builder, Query>,
                                                      Query extends PagedQuery> {
        protected Integer pageNumber;
        protected Integer pageSize;
        protected List<Sort> sortBy = new ArrayList<>();

        protected PagedQueryBuilder() {
        }

        public abstract Query build();

        public Builder pageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
            return self();
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return self();
        }

        public Builder sortBy(String property, Sort.Direction direction) {
            this.sortBy.add(Sort.by(property, direction));
            return self();
        }

        public Builder sortBy(Collection<Sort> sorts) {
            this.sortBy.addAll(sorts);
            return self();
        }

        protected abstract Builder self();
    }
}
