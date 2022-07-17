package org.oopscraft.apps.batch.item.db;

import ch.qos.logback.classic.Level;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.session.*;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MybatisDbItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private PlatformTransactionManager transactionManager;

    @Setter
    @Getter
    private SqlSessionFactory sqlSessionFactory;

    @Getter
    @Setter
    private DataSource dataSource;

    @Getter
    @Setter
    private Class<?> mapperClass;

    @Getter
    @Setter
    private String mapperMethod;

    @Getter
    @Setter
    private Map<String, Object> parameters;

    @Getter
    @Setter
    private String dataSourceKey;

    private SqlSession sqlSession;

    private Cursor<T> cursor;

    private Iterator<T> cursorIterator;

    @Builder.Default
    private int readCount = 0;

    @Override
    protected void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [START] MybatisDbItemReader");
        log.info("| name: {}", name);
        log.info("| mapperClass: {}", mapperClass);
        log.info("| mapperMethod: {}", mapperMethod);
        log.info("| parameters: {}", parameters);
        log.info("{}", StringUtils.repeat("-",80));

        // checks validation
        Assert.notNull(name, "name must not be null");
        Assert.notNull(sqlSessionFactory, "sqlSessionFactory must not be null");
        Assert.notNull(dataSource, "dataSource must not be null");
        Assert.notNull(mapperClass, "mapperClass must not be null");
        Assert.notNull(mapperMethod, "mapperMethod must not be null");


        // sets name
        super.setName(name);

        try {
            if(dataSourceKey != null) {
                RoutingDataSource.setKey(dataSourceKey);
            }

            ch.qos.logback.classic.Logger sqlLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(mapperClass);
            Level sqlLoggerLevel = sqlLogger.getLevel();
            try {
                sqlLogger.setLevel(Level.DEBUG);

                // SpringManaged Transaction 이므로 처리 중 commit 발생 시 cursor 도 종료되는 문제가 있음으로 Cursor 는 분리된 트랜잭션으로 설정
                Configuration configuration = sqlSessionFactory.getConfiguration();
                TransactionFactory transactionFactory = new ManagedTransactionFactory();
                Transaction transaction = transactionFactory.newTransaction(dataSource, TransactionIsolationLevel.READ_COMMITTED, false);
                Executor executor = configuration.newExecutor(transaction, ExecutorType.SIMPLE);
                sqlSession = new DefaultSqlSession(configuration, executor, false);
                String statementId = String.format("%s.%s", mapperClass.getName(), mapperMethod);
                cursor = sqlSession.selectCursor(statementId, parameters);
                cursorIterator = cursor.iterator();
            }finally{
                sqlLogger.setLevel(sqlLoggerLevel);
            }

        }finally{
            if(dataSourceKey != null) {
                RoutingDataSource.clearKey();
            }
        }
    }

    @Override
    protected T doRead() throws Exception {
        T next = null;
        if (cursorIterator.hasNext()) {
            next = cursorIterator.next();
            readCount ++;
        }
        return next;
    }

    @Override
    protected void doClose() throws Exception {
        if (cursor != null) {
            cursor.close();
        }
        if (sqlSession != null) {
            sqlSession.close();
        }
        cursorIterator = null;

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [END] MybatisDbItemReader");
        log.info("| name: {}", name);
        log.info("| mapperClass: {}", mapperClass);
        log.info("| mapperMethod: {}", mapperMethod);
        log.info("| parameters: {}", parameters);
        log.info("| readCount: {}", readCount);
        log.info("{}", StringUtils.repeat("-",80));
    }


    /**
     * MybatisDbItemReaderBuilder
     * @param <T>
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class MybatisDbItemReaderBuilder<T> {

        private String name;

        private PlatformTransactionManager transactionManager;

        private SqlSessionFactory sqlSessionFactory;

        private DataSource dataSource;

        private Class mapperClass;

        private String mapperMethod;

        private Map<String, Object> parameters = new LinkedHashMap<>();

        private String dataSourceKey;

        /**
         * parameter
         * @param name
         * @param value
         * @return
         */
        public MybatisDbItemReaderBuilder<T> parameter(String name, Object value) {
            parameters.put(name, value);
            return this;
        }

        /**
         * build
         * @return
         */
        public MybatisDbItemReader<T> build() {
            MybatisDbItemReader<T> instance = new MybatisDbItemReader<T>();
            Optional.ofNullable(name).ifPresent(value -> instance.setName(value));
            Optional.ofNullable(transactionManager).ifPresent(value -> instance.setTransactionManager(value));
            Optional.ofNullable(sqlSessionFactory).ifPresent(value -> instance.setSqlSessionFactory(value));
            Optional.ofNullable(dataSource).ifPresent(value -> instance.setDataSource(value));
            Optional.ofNullable(mapperClass).ifPresent(value -> instance.setMapperClass(value));
            Optional.ofNullable(mapperMethod).ifPresent(value -> instance.setMapperMethod(value));
            Optional.ofNullable(parameters).ifPresent(value -> instance.setParameters(value));
            Optional.ofNullable(dataSourceKey).ifPresent(value -> instance.setDataSourceKey(value));
            return instance;
        }
    }

    /**
     * builder
     * @param <T>
     * @return
     */
    public static <T> MybatisDbItemReaderBuilder<T> builder() {
        return new MybatisDbItemReaderBuilder<T>();
    }
}
