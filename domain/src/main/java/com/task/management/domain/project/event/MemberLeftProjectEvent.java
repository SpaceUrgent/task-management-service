package com.task.management.domain.project.event;

import com.task.management.domain.shared.event.AbstractDomainEvent;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MemberLeftProjectEvent extends AbstractDomainEvent {
     private UserId memberId;
     private ProjectId projectId;

     @Builder
     public MemberLeftProjectEvent(UserId memberId, ProjectId projectId) {
         super();
         this.memberId = parameterRequired(memberId, "Member id");
         this.projectId = parameterRequired(projectId, "Project id");
     }
}
