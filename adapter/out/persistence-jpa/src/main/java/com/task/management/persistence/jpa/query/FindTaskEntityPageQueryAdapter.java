package com.task.management.persistence.jpa.query;

import com.task.management.domain.common.Sort;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.in.query.FindTasksQuery;
import com.task.management.persistence.jpa.entity.TaskEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class FindTaskEntityPageQueryAdapter implements FindPageQuery<TaskEntity> {
    private final FindTasksQuery query;

    public FindTaskEntityPageQueryAdapter(FindTasksQuery query) {
        this.query = Objects.requireNonNull(query, "Find task query is required");
    }

    @Override
    public CriteriaQuery<TaskEntity> toQuery(CriteriaBuilder criteriaBuilder) {
        criteriaBuilderRequired(criteriaBuilder);
        final var selectQuery = criteriaBuilder.createQuery(TaskEntity.class);
        final var selectFrom = selectQuery.from(TaskEntity.class);
        selectQuery.where(criteriaPredicateArray(criteriaBuilder, selectFrom));
        final var orders = ordersArray(criteriaBuilder, selectFrom);
        selectQuery.orderBy(orders);
        return selectQuery;
    }

    @Override
    public CriteriaQuery<Long> toCountQuery(CriteriaBuilder criteriaBuilder) {
        criteriaBuilderRequired(criteriaBuilder);
        final var countQuery = criteriaBuilder.createQuery(Long.class);
        final var countFrom = countQuery.from(TaskEntity.class);
        countQuery.select(criteriaBuilder.count(countFrom))
                .where(criteriaPredicateArray(criteriaBuilder, countFrom));
        return countQuery;
    }

    @Override
    public int pageIndex() {
        return this.query.getPageNumber() - 1;
    }

    @Override
    public int size() {
        return this.query.getPageSize();
    }

    private Predicate[] criteriaPredicateArray(CriteriaBuilder criteriaBuilder,
                                               Root<TaskEntity> root) {
        final var predicates = new ArrayList<Predicate>();
        final var joinProjects = root.join("project", JoinType.INNER);
        predicates.add(criteriaBuilder.equal(joinProjects.get("id"), projectId()));
        if (nonNull(assigneeId())) {
            final var countJoinAssignedTo = root.join("assignee", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(countJoinAssignedTo.get("id"), assigneeId()));
        }
        if (nonNull(statusIn()) && !statusIn().isEmpty()) {
            predicates.add(root.get("status").in(statusIn()));
        }
        return predicates.toArray(new Predicate[]{});
    }

    private Long projectId() {
        return this.query.getProjectId().value();
    }

    private Long assigneeId() {
        return Optional.ofNullable(this.query.getAssigneeId()).map(ProjectUserId::value).orElse(null);
    }

    private Set<String> statusIn() {
        return Optional.ofNullable(this.query.getStatuses())
                .map(statuses -> statuses.stream().map(TaskStatus::name).collect(Collectors.toSet()))
                .orElse(null);
    }

    private Order[] ordersArray(CriteriaBuilder criteriaBuilder, Root<?> from) {
        return this.query.getSortBy().stream()
                .map(sortBy -> toOrder(sortBy, criteriaBuilder, from))
                .toArray(Order[]::new);
    }

    private Order toOrder(Sort sort, CriteriaBuilder criteriaBuilder, Root<?> from) {
        final var orderByProperty = from.get(sort.getProperty());
        return Sort.Direction.ASC == sort.getDirection()
                ? criteriaBuilder.asc(orderByProperty)
                : criteriaBuilder.desc(orderByProperty);
    }
}
