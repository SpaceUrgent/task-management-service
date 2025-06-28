package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.MemberEntity;

import java.util.Optional;

public interface MemberEntityDao {

    Optional<MemberEntity> findById(MemberEntity.MemberPK id);

    MemberEntity save(MemberEntity entity);

    void delete(MemberEntity id);
}
