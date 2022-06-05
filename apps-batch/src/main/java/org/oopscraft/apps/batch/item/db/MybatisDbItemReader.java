package org.oopscraft.apps.batch.item.db;

import ch.qos.logback.classic.Level;
import lombok.Builder;
import lombok.Singular;
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
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Builder
public class MybatisDbItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> {

    private String name;

    private SqlSessionFactory sqlSessionFactory;

    private DataSource dataSource;

    private Class<?> mapperClass;

    private String mapperMethod;

    @Singular
    private Map<String, Object> parameters;

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

}
