package org.oopscraft.apps.batch.item.db;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JpaDbItemWriter<T> extends JpaItemWriter<T> implements ItemStreamWriter<T> {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private PlatformTransactionManager transactionManager;

    @Setter
    @Getter
    private EntityManagerFactory entityManagerFactory;

    @Setter
    @Getter
    private String dataSourceKey;

    public int writeCount = 0;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [START] JpaDbItemWriter");
        log.info("| name: {}", name);
        log.info("| dataSourceKey: {}", dataSourceKey);
        log.info("{}", StringUtils.repeat("-",80));

        // checks validation
        Assert.notNull(name, "name must not be null");
        Assert.notNull(transactionManager, "transactionManager must not be null");
        Assert.notNull(entityManagerFactory, "entityManagerFactory must not be null");

        // open reader
        super.setEntityManagerFactory(entityManagerFactory);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.debug("JpaDbItemWriter.update():{}", executionContext);
    }

    /**
     * write
     * @param items
     * @throws Exception
     */
    @Override
    public void write(List<? extends T> items) {
        if(dataSourceKey != null) {
            DefaultTransactionDefinition definition = null;
            TransactionStatus status = null;
            try {
                // switch dataSource key
                RoutingDataSource.setKey(dataSourceKey);

                // creates new transaction
                definition = new DefaultTransactionDefinition();
                definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                status = transactionManager.getTransaction(definition);

                // executes write
                internalWrite(items);

                // commit
                transactionManager.commit(status);
            }catch(Exception e){
                // rollback
                transactionManager.rollback(status);
            }finally{
                // restore dataSourceKey
                RoutingDataSource.clearKey();
            }
        }else{
            internalWrite(items);
        }
    }

    /**
     * internalWrite
     * @param items
     */
    protected void internalWrite(List<? extends T> items) {
        for(T item : items){
            write(item);
        }
    }

    /**
     * write
     * @param item
     * @throws Exception
     */
    public void write(T item) {
        super.write(Arrays.asList(item));
        writeCount ++;
    }

    @Override
    public void close() throws ItemStreamException {
        // logging
        log.info(StringUtils.repeat("-", 80));
        log.info("| [END] JpaDbItemWriter");
        log.info("| name: {}", name);
        log.info("| dataSourceKey: {}", dataSourceKey);
        log.info("| writeCount: {}", writeCount);
        log.info(StringUtils.repeat("-", 80));
    }

    /**
     * JpaDbItemWriterBuilder
     * @param <T>
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class JpaDbItemWriterBuilder<T> {
        private String name;
        private PlatformTransactionManager transactionManager;
        private EntityManagerFactory entityManagerFactory;
        private String dataSourceKey;
        public JpaDbItemWriter<T> build() {
            JpaDbItemWriter<T> instance = new JpaDbItemWriter<T>();
            if(name != null) instance.setName(name);
            if(transactionManager != null) instance.setTransactionManager(transactionManager);
            if(entityManagerFactory != null) instance.setEntityManagerFactory(entityManagerFactory);
            if(dataSourceKey != null) instance.setDataSourceKey(dataSourceKey);
            return instance;
        }
    }

    /**
     * builder
     * @param <T>
     * @return
     */
    public static <T> JpaDbItemWriterBuilder<T> builder() {
        return new JpaDbItemWriterBuilder<T>();
    }

}
