package org.oopscraft.apps.core.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RoutingTransactionUtils {

    private final PlatformTransactionManager transactionManager;

    /**
     * executeWithoutResult
     * @param transactionManager
     * @param dataSourceKey
     * @param consumer
     */
    public static void executeWithoutResult(PlatformTransactionManager transactionManager, String dataSourceKey, Consumer<TransactionStatus> consumer) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        transactionDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        try {
            RoutingDataSource.setKey(dataSourceKey);
            transactionTemplate.executeWithoutResult(consumer);
        }finally{
            RoutingDataSource.clearKey();
        }
    }

    /**
     * executeWithoutResult
     * @param dataSourceKey
     * @param consumer
     */
    public void executeWithoutResult(String dataSourceKey, Consumer<TransactionStatus> consumer) {
        executeWithoutResult(transactionManager, dataSourceKey, consumer);
    }

    /**
     * execute
     * @param transactionManager
     * @param dataSourceKey
     * @param transactionCallback
     * @return
     * @param <T>
     */
    public static <T> T execute(PlatformTransactionManager transactionManager, String dataSourceKey, TransactionCallback<T> transactionCallback) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        transactionDefinition.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        try {
            RoutingDataSource.setKey(dataSourceKey);
            return transactionTemplate.execute(transactionCallback);
        }finally{
            RoutingDataSource.clearKey();
        }

    }

    /**
     * execute
     * @param dataSourceKey
     * @param transactionCallback
     * @return
     * @param <T>
     */
    public <T> T execute(String dataSourceKey, TransactionCallback<T> transactionCallback) {
        return execute(transactionManager, dataSourceKey, transactionCallback);
    }

}
