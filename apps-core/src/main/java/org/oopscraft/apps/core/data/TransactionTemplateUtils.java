package org.oopscraft.apps.core.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TransactionTemplateUtils {

    private final PlatformTransactionManager transactionManager;

    /**
     * executeWithoutResult
     * @param transactionManager
     * @param propagation
     * @param consumer
     */
    public static void executeWithoutResult(PlatformTransactionManager transactionManager, Propagation propagation, Consumer<TransactionStatus> consumer) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(propagation.value());
        transactionDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        transactionTemplate.executeWithoutResult(consumer);
    }

    /**
     * executeWithoutResult
     * @param propagation
     * @param consumer
     */
    public void executeWithoutResult(Propagation propagation, Consumer<TransactionStatus> consumer) {
        executeWithoutResult(transactionManager, propagation, consumer);
    }

    /**
     * execute
     * @param transactionManager
     * @param propagation
     * @param transactionCallback
     * @return
     * @param <T>
     */
    public static <T> T execute(PlatformTransactionManager transactionManager, Propagation propagation, TransactionCallback<T> transactionCallback) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(propagation.value());
        transactionDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        return transactionTemplate.execute(transactionCallback);
    }

    /**
     * execute
     * @param propagation
     * @param transactionCallback
     * @return
     * @param <T>
     */
    public <T> T execute(Propagation propagation, TransactionCallback<T> transactionCallback) {
        return execute(transactionManager, propagation, transactionCallback);
    }

}
