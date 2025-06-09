package com.task.management.persistence.jpa.query;

import com.task.management.application.common.query.Sort;
import com.task.management.application.dashboard.query.FindAssignedDashboardTasksQuery;
import com.task.management.persistence.jpa.entity.TaskEntity;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class FindAssignedDashboardTasksQueryAdapter implements FindPageQuery<TaskEntity> {
    private final FindAssignedDashboardTasksQuery query;

    public FindAssignedDashboardTasksQueryAdapter(FindAssignedDashboardTasksQuery query) {
        this.query = query;
    }

    @Override
    public int pageIndex() {
        return this.query.getPageNumber() - 1;
    }

    @Override
    public int size() {
        return this.query.getPageSize();
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

    private Predicate[] criteriaPredicateArray(CriteriaBuilder criteriaBuilder,
                                               Root<TaskEntity> root) {
        final var predicates = new ArrayList<Predicate>();
        final var joinAssignee = root.join("assignee", JoinType.INNER);
        predicates.add(criteriaBuilder.equal(joinAssignee.get("id"), assigneeId()));
        if (query.isOverdueOnly()) {
            Path<LocalDate> dueDatePath = root.get("dueDate");
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.isNull(dueDatePath),
                criteriaBuilder.lessThan(dueDatePath, LocalDate.now())
            ));
        }
        final var joinStatus = root.join("status", JoinType.LEFT);
        predicates.add(criteriaBuilder.notEqual(joinStatus.get("isFinal"), true));
        return predicates.toArray(new Predicate[]{});
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

    private Long assigneeId() {
        return this.query.getAssignee().value();
    }
}
