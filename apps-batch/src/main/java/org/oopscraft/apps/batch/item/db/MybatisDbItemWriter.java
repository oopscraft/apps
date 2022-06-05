package org.oopscraft.apps.batch.item.db;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
public class MybatisDbItemWriter<T> implements ItemStreamWriter<T> {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private PlatformTransactionManager transactionManager;

    @Setter
    @Getter
    private SqlSessionFactory sqlSessionFactory;

    @Setter
    @Getter
    private Class mapperClass;

    @Setter
    @Getter
    private String mapperMethod;

    @Setter
    @Getter
    private String dataSourceKey;

    private SqlSessionTemplate sqlSessionTemplate;

    private Converter<T, T> itemToParameterConverter =  new Converter<T, T>() {
        @Override
        public T convert(T source) {
            return source;
        }
    };

    private int writeCount = 0;

    /**
     * open
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [START] MybatisDbItemWriter");
        log.info("| name: {}", name);
        log.info("| mapperClass: {}", mapperClass);
        log.info("| mapperMethod: {}", mapperMethod);
        log.info("{}", StringUtils.repeat("-",80));

        // checks validation
        Assert.notNull(name, "name must not be null");
        Assert.notNull(transactionManager, "transactionManager must not be null");
        Assert.notNull(sqlSessionFactory, "sqlSessionFactory must not be null");
        Assert.notNull(mapperClass, "mapperClass must not be null");
        Assert.notNull(mapperMethod, "mapperMethod must not be null");

        // creates sqlSessionTemplate
        sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.SIMPLE);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.debug("MybatisDbItemWriter.update():{}", executionContext);
    }

    /**
     * write
     * @param items
     */
    @Override
    public void write(final List<? extends T> items) {
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
    protected void internalWrite(final List<? extends T> items) {
        for (T item : items) {
            write(item);
        }
    }

    /**
     * write
     * @param item
     */
    public void write(T item) {
        String statementId = String.format("%s.%s", mapperClass.getName(), mapperMethod);
        sqlSessionTemplate.update(statementId, itemToParameterConverter.convert(item));
        writeCount ++;
    }

    /**
     * close
     */
    @Override
    public void close() {

        // closes resources
        try {
            if(sqlSessionTemplate != null){
                sqlSessionTemplate.close();
            }
        }catch(Exception ignore){
            log.warn(ignore.getMessage());
        }

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [END] MybatisDbItemWriter");
        log.info("| name: {}", name);
        log.info("| mapperClass: {}", mapperClass);
        log.info("| mapperMethod: {}", mapperMethod);
        log.info("| writeCount: {}", writeCount);
        log.info("{}", StringUtils.repeat("-",80));
    }

    /**
     * MybatisDbItemWriterBuilder
     * @param <T> type
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class MybatisDbItemWriterBuilder<T> {
        private String name;
        private PlatformTransactionManager transactionManager;
        private SqlSessionFactory sqlSessionFactory;
        private Class mapperClass;
        private String mapperMethod;
        private String dataSourceKey;
        public MybatisDbItemWriter<T> build() {
            MybatisDbItemWriter<T> instance = new MybatisDbItemWriter<T>();
            if(name != null) instance.setName(name);
            if(transactionManager != null) instance.setTransactionManager(transactionManager);
            if(sqlSessionFactory != null) instance.setSqlSessionFactory(sqlSessionFactory);
            if(mapperClass != null) instance.setMapperClass(mapperClass);
            if(mapperMethod != null) instance.setMapperMethod(mapperMethod);
            if(dataSourceKey != null) instance.setDataSourceKey(dataSourceKey);
            return instance;
        }
    }

    /**
     * builder
     * @param <T>
     * @return
     */
    public static <T> MybatisDbItemWriterBuilder<T> builder() {
        return new MybatisDbItemWriterBuilder<T>();
    }

}