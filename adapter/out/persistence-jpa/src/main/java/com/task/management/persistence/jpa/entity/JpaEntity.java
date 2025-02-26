package com.task.management.persistence.jpa.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class JpaEntity<ID> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected ID id;

    protected JpaEntity() {
    }
}
