package com.task.management.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
@Embeddable
public class AvailableTaskStatus {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer position;
    @Column(name = "is_final")
    private boolean isFinal = false;

    protected AvailableTaskStatus() {
    }

    @Builder
    public AvailableTaskStatus(String name,
                               Integer position) {
        this.name = notBlank(name, "Name");
        this.position = parameterRequired(position, "Position");
    }
}
