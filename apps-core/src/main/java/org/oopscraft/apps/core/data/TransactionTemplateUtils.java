package org.oopscraft.apps.core.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TransactionTemplateUtils {

    private final PlatformTransactionManager transactionManager;

    /**
     * getTransactionTemplate
     * @return
     */
    public TransactionTemplate getTransactionTemplate() {
        return new TransactionTemplate(transactionManager);
    }

    /**
     * execute
     * @param action
     * @param <T>
     * @return
     * @throws TransactionException
     */
    public <T> T execute(Propagation propagation, TransactionCallback<T> action) throws TransactionException {
        TransactionTemplate transactionTemplate = getTransactionTemplate();
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate.execute(action);
    }

    /**
     * executeWithoutResult
     * @param action
     * @throws TransactionException
     */
    public void executeWithoutResult(Propagation propagation, Consumer<TransactionStatus> action) throws TransactionException {
        TransactionTemplate transactionTemplate = getTransactionTemplate();
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(action);
    }

}