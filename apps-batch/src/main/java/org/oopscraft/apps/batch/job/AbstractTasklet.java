package org.oopscraft.apps.batch.job;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.db.MybatisDbItemReader;
import org.oopscraft.apps.batch.item.db.MybatisDbItemWriter;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.*;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.UUID;

@Slf4j
public abstract class AbstractTasklet implements Tasklet {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoutingDataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    public EntityManagerFactory entityManagerFactory;

    @Autowired
    public SqlSessionFactory sqlSessionFactory;

    @Setter
    private String dataSourceKey;

    private TransactionStatus transactionStatus;

    private StepContribution stepContribution;

    private int readCount = 0;

    private int filterCount = 0;

    private int writeCount = 0;

    private int commitInterval = 1;

    private int commitIntervalBuffer = 0;

    private int commitCount = 0;

    private int rollbackCount = 0;

    /**
     * getApplicationContext
     * @return
     */
    public final ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * getBatchContext
     * @return
     */
    public final BatchContext getBatchContext(){
        return applicationContext.getBean(BatchContext.class);
    }

    @Override
    public final RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            // switches data source
            if (dataSourceKey != null) {
                dataSource.switchDefaultDataSource(dataSourceKey);
            }

            this.stepContribution = stepContribution;
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = transactionManager.getTransaction((TransactionDefinition) defaultTransactionDefinition);
            stepContribution.setExitStatus(ExitStatus.EXECUTING);
            ExecutionContext executionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
            BatchContext batchContext = applicationContext.getBean(BatchContext.class);
            try {
                doExecute(batchContext, executionContext);
                this.transactionManager.commit(this.transactionStatus);
            } catch (Throwable e) {
                this.transactionManager.rollback(this.transactionStatus);
                stepContribution.setExitStatus(ExitStatus.FAILED);
                log.error("Error Occurred in batch Job execution", e);
                throw new Exception(e);
            } finally {
                closeAll();
            }

            // exit
            stepContribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }finally{
            if(dataSourceKey != null){
                dataSource.restoreDefaultDataSource();
            }
        }
    }

    /**
     * doExecute
     * @param batchContext
     * @param executionContext
     * @throws Exception
     */
    public abstract void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception;

    /**
     * increaseReadCount
     */
    public final void increaseReadCount() {
        this.readCount ++;
    }

    /**
     * increaseWriteCount
     */
    public final void increaseWriteCount() {
        this.writeCount ++;
    }

    /**
     * increaseFilterCount
     */
    public final void increaseFilterCount() {
        this.filterCount++;
    }

    /**
     * commit with commit interval
     * @param commitInterval
     */
    public final void commit(int commitInterval) {
        commitIntervalBuffer++;
        if(commitIntervalBuffer >= commitInterval){
            commit();
        }
    }

    /**
     * Commits transaction.
     */
    public final void commit() {
        transactionManager.commit(transactionStatus);
        commitCount++;
        commitIntervalBuffer = 0;
        stepContribution.getStepExecution().setCommitCount(commitCount);
        stepContribution.getStepExecution().setReadCount(readCount);
        stepContribution.getStepExecution().setFilterCount(filterCount);
        stepContribution.getStepExecution().setWriteCount(writeCount);
        log.debug("commit - readCount:{}, writeCount:{}, filterCount:{}", readCount, writeCount, filterCount);
        flushAll();

        // creates new transaction
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionStatus = transactionManager.getTransaction((TransactionDefinition) defaultTransactionDefinition);
    }

    /**
     * Rollbacks transaction
     */
    public final void rollback() {
        log.debug("rollback");
        transactionManager.rollback(transactionStatus);
        rollbackCount++;
        stepContribution.getStepExecution().setRollbackCount(rollbackCount);

        // creates new transaction
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionStatus = transactionManager.getTransaction((TransactionDefinition) defaultTransactionDefinition);
    }

    /**
     * flushAll
     */
    private final void flushAll() {
        // TODO
    }

    /**
     * closeAll
     */
    private final void closeAll() {
        // TODO
    }

    /**
     * getDbItemReaderBuilder
     * @param itemType
     * @return
     */
    public final <T> MybatisDbItemReader.MybatisDbItemReaderBuilder<T> createMybatisDbItemReaderBuilder(Class<T> itemType) {
        return MybatisDbItemReader.<T>builder()
                .name(UUID.randomUUID().toString())
                .sqlSessionFactory(applicationContext.getBean(SqlSessionFactory.class))
                .dataSource(applicationContext.getBean(DataSource.class));
    }

    /**
     * getDbItemWriterBuilder
     * @param itemType
     * @return
     */
    public final <T> MybatisDbItemWriter.MybatisDbItemWriterBuilder<T> createMybatisDbItemWriterBuilder(Class<T> itemType) {
        return MybatisDbItemWriter.<T>builder()
                .name(UUID.randomUUID().toString())
                .sqlSessionFactory(applicationContext.getBean(SqlSessionFactory.class));
    }

    /**
     * createQueryDslDbItemReader
     * @param itemType
     * @return
     */
    public final <T> QueryDslDbItemReader.QueryDslDbItemReaderBuilder<T> createQueryDslDbItemReader(Class<T>itemType) {
        return QueryDslDbItemReader.<T>builder()
                .name(UUID.randomUUID().toString())
                .entityManagerFactory(getApplicationContext().getBean(EntityManagerFactory.class));
    }

    /**
     * createJpaDbItemWriterBuilder
     * @param itemType
     * @param <T>
     * @return
     */
    public final <T> JpaDbItemWriter.JpaDbItemWriterBuilder<T> createJpaDbItemWriterBuilder(Class<T> itemType) {
        return JpaDbItemWriter.<T>builder()
                .name(UUID.randomUUID().toString())
                .entityManagerFactory(entityManagerFactory);
    }

    /**
     * getDelimiterFileItemReaderBuilder
     * @param itemType
     * @return
     */
    public final <T> DelimiterFileItemReader.DelimiterFileItemReaderBuilder<T> createDelimiterFileItemReaderBuilder(Class<T> itemType){
        return DelimiterFileItemReader.<T>builder()
                .itemType(itemType)
                .name(UUID.randomUUID().toString());
    }

    /**
     * getDelimiterFileItemWriterBuilder
     * @param itemType
     * @return
     */
    public final <T> DelimiterFileItemWriter.DelimiterFileItemWriterBuilder<T> createDelimiterFileItemWriterBuilder(Class<T> itemType){
        return DelimiterFileItemWriter.<T>builder()
                .itemType(itemType)
                .name(UUID.randomUUID().toString());
    }

    /**
     * getFixedLengthFileItemReaderBuilder
     * @param itemType
     * @return
     */
    public final <T> FixedLengthFileItemReader.FixedLengthFileItemReaderBuilder<T> createFixedLengthFileItemReaderBuilder(Class<T> itemType) {
        return FixedLengthFileItemReader.<T>builder()
                .itemType(itemType)
                .name(UUID.randomUUID().toString());
    }

    /**
     * getFixedLengthFileItemWriterBuilder
     * @param itemType itemType
     * @return FixedLengthFileItemWriter.FixedLengthFileItemWriterBuilder
     */
    public final <T> FixedLengthFileItemWriter.FixedLengthFileItemWriterBuilder<T> createFixedLengthFileItemWriterBuilder(Class<T> itemType){
        return FixedLengthFileItemWriter.<T>builder()
                .itemType(itemType)
                .name(UUID.randomUUID().toString());
    }

    /**
     * createStringFileItemReaderBuilder
     * @return
     */
    public final StringFileItemReader.StringFileItemReaderBuilder createStringFileItemReaderBuilder() {
        return StringFileItemReader.builder()
                .name(UUID.randomUUID().toString());
    }

    /**
     * StringFileItemWriterBuilder
     * @return
     */
    public final StringFileItemWriter.StringFileItemWriterBuilder StringFileItemWriterBuilder(){
        return StringFileItemWriter.builder()
                .name(UUID.randomUUID().toString());
    }

}
