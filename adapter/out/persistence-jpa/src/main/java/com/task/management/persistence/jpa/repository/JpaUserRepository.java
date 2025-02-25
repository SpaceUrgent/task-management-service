package com.task.management.persistence.jpa.repository;

import com.task.management.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Query("""
            from UserEntity user\s
            inner join user.projects project\s
            where user.id = :userId and project.id = :projectId
            """)
    Optional<UserEntity> findMember(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("""
            from UserEntity user\s
            inner join user.projects project\s
            where project.id = :projectId
            """)
    List<UserEntity> findByProject(@Param("projectId") Long projectId);

    boolean existsByEmail(String email);
}
