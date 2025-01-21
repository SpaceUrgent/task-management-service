package com.task.management.persistence.jpa.repository;

import com.task.management.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
