package com.task.management.spring;

import com.task.management.application.common.annotation.UseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class TestUseCaseService {

    @UseCase
    public boolean isActiveTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
