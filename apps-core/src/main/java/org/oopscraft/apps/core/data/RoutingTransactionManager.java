package org.oopscraft.apps.core.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.Closeable;

@Slf4j
public class RoutingTransactionManager implements Closeable {

    // transaction manager
    private static PlatformTransactionManager transactionManager;

    // transaction
    private DefaultTransactionDefinition txDefinition;
    private TransactionStatus txStatus;

    public static void setTransactionManager(PlatformTransactionManager transactionManagerObj) {
        transactionManager = transactionManagerObj;
    }

    public static RoutingTransactionManager beginTransaction(String dataSourceKey) {
        return new RoutingTransactionManager(dataSourceKey);
    }

    /**
     * constructor
     * @param dataSourceKey
     */
    private RoutingTransactionManager(String dataSourceKey){
        log.warn("DataSourceContextHolder.beginTransaction[{}]", dataSourceKey);
        RoutingDataSource.setKey(dataSourceKey);
        txDefinition = new DefaultTransactionDefinition();
        txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txStatus = transactionManager.getTransaction(txDefinition);
    }

    /**
     * commit
     */
    public void commit() {
        transactionManager.commit(txStatus);
        if (txStatus.isCompleted()) {
            txDefinition = new DefaultTransactionDefinition();
            txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            txStatus = transactionManager.getTransaction(txDefinition);
        }
    }

    /**
     * roll back
     */
    public void rollback() {
        transactionManager.rollback(txStatus);
        if (txStatus.isCompleted()) {
            txDefinition = new DefaultTransactionDefinition();
            txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            txStatus = transactionManager.getTransaction(txDefinition);
        }
    }

    /**
     * release
     */
    public void endTransaction() {
        log.warn("DataSourceContextHolder.release[{}]", RoutingDataSource.getKey());
        try {
            if (!txStatus.isCompleted()) {
                transactionManager.rollback(txStatus);
            }
        }finally {
            RoutingDataSource.clearKey();
        }
    }

    @Override
    public void close() {
        endTransaction();
    }
}
