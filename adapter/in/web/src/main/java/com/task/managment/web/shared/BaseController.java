package com.task.managment.web.shared;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.managment.web.security.SecuredUser;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected UserId actor() {
        return new UserId(securedUser().getId());
    }

    protected SecuredUser securedUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SecuredUser) authentication.getDetails();
    }
}
