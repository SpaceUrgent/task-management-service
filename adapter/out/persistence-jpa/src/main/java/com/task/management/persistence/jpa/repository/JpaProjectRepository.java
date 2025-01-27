package com.task.management.persistence.jpa.repository;

import com.task.management.persistence.jpa.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT project FROM ProjectEntity project JOIN project.members member WHERE member.id = :memberId")
    Page<ProjectEntity> findByMember(@Param("memberId") Long memberId, Pageable pageable);

}
