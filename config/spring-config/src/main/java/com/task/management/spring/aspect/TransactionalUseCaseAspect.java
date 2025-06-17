package com.task.management.spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
public class TransactionalUseCaseAspect {

    private final PlatformTransactionManager transactionManager;

    public TransactionalUseCaseAspect(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    @Around("@annotation(com.task.management.application.common.annotation.UseCase)")
    public Object runWithTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        final var definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        final var status = transactionManager.getTransaction(definition);
        try {
            Object result = joinPoint.proceed();
            transactionManager.commit(status);
            return result;
        } catch (Throwable throwable) {
            transactionManager.rollback(status);
            throw throwable;
        }
    }
}
