package org.oopscraft.apps.batch.job;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.db.MybatisDbItemReader;
import org.oopscraft.apps.batch.item.db.MybatisDbItemWriter;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.*;
import org.oopscraft.apps.batch.listener.JobListener;
import org.oopscraft.apps.batch.listener.StepListener;
import org.oopscraft.apps.batch.tasklet.AbstractTasklet;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.UUID;

@Slf4j
public abstract class AbstractJob extends SimpleJob implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Getter
    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    @Getter
    protected SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Getter
    protected StepBuilderFactory stepBuilderFactory;

    /**
     * setApplicationContext
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * getApplicationContext
     * @return
     */
    public final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    /**
     * initialize
     */
    public abstract void initialize(BatchContext batchContext);

    @Override
    public void afterPropertiesSet() {
        this.setName(this.getClass().getName());
        this.setJobRepository(applicationContext.getBean(JobRepository.class));
        this.registerJobExecutionListener(new JobListener());
        this.initialize(applicationContext.getBean(BatchContext.class));
    }

    /**
     * getBatchContext
     * @return
     */
    public final BatchContext getBatchContext(){
        return applicationContext.getBean(BatchContext.class);
    }

    /**
     * addTasklet
     * @param tasklet
     */
    public void addTasklet(AbstractTasklet tasklet) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(tasklet);
        TaskletStep taskletStep = stepBuilderFactory.get(tasklet.getClass().getName())
                .tasklet(tasklet)
                .listener((StepExecutionListener) new StepListener())
                .build();
        super.addStep(taskletStep);
    }

    /**
     * addStep
     * @param step
     */
    public void addStep(Step step){
        applicationContext.getAutowireCapableBeanFactory().autowireBean(step);
        ((AbstractStep) step).registerStepExecutionListener(new StepListener());
        ((TaskletStep) step).registerChunkListener(new StepListener());
        super.addStep(step);
    }

    /**
     * addTaskletWithDataSourceKey
     * @param tasklet
     * @param dataSourceKey
     */
    public void addTaskletWithDataSourceKey(AbstractTasklet tasklet, String dataSourceKey) {
        tasklet.setDataSourceKey(dataSourceKey);
        addTasklet(tasklet);
    }

    /**
     * getDbItemReaderBuilder
     * @param itemType
     * @return
     */
    public final <T> MybatisDbItemReader.MybatisDbItemReaderBuilder<T> createMybatisDbItemReaderBuilder(Class<T> itemType) {
        return MybatisDbItemReader.<T>builder()
                .name(UUID.randomUUID().toString())
                .sqlSessionFactory(sqlSessionFactory)
                .dataSource(dataSource)
                .dataSourceKey("readOnly");
    }

    /**
     * getDbItemWriterBuilder
     * @param itemType
     * @return
     */
    public final <T> MybatisDbItemWriter.MybatisDbItemWriterBuilder<T> createMybatisDbItemWriterBuilder(Class<T> itemType) {
        return MybatisDbItemWriter.<T>builder()
                .name(UUID.randomUUID().toString())
                .sqlSessionFactory(sqlSessionFactory);
    }

    /**
     * createQueryDslDbItemReader
     * @param itemType
     * @return
     */
    public final <T> QueryDslDbItemReader.QueryDslDbItemReaderBuilder<T> createQueryDslDbItemReader(Class<T>itemType) {
        return QueryDslDbItemReader.<T>builder()
                .name(UUID.randomUUID().toString())
                .entityManagerFactory(entityManagerFactory)
                .dataSourceKey("readOnly");
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
     * createStringFileItemWriterBuilder
     * @return
     */
    public final StringFileItemWriter.StringFileItemWriterBuilder createStringFileItemWriterBuilder(){
        return StringFileItemWriter.builder()
                .name(UUID.randomUUID().toString());
    }

}
