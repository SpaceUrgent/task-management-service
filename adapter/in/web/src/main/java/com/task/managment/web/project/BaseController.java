package com.task.managment.web.project;

import com.task.management.domain.project.model.ProjectUserId;
import com.task.managment.web.security.SecuredUser;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected ProjectUserId actorId() {
        return new ProjectUserId(actor().getId());
    }

    protected SecuredUser actor() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
