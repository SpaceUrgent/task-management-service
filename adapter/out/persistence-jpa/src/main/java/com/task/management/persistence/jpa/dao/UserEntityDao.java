package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserEntityDao extends EntityDao<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean isMember(Long userId, Long projectId);

    List<UserEntity> findByProject(Long projectId);

    boolean existsByEmail(String email);
}
