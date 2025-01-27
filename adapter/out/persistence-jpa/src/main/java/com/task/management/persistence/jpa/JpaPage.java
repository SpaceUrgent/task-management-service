package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static java.util.Objects.requireNonNull;

public class JpaPage extends PageRequest {

    protected JpaPage(int pageNumber, int pageSize, Sort sort) {
        super(pageNumber, pageSize, sort);
    }

    public static JpaPage of(PageQuery pageQuery) {
        requireNonNull(pageQuery, "Page query is required");
        return new JpaPage(pageQuery.getPageNumber() - 1, pageQuery.getPageSize(), Sort.unsorted());
    }
}
