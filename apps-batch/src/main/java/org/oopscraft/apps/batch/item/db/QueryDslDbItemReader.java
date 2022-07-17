package org.oopscraft.apps.batch.item.db;

import ch.qos.logback.classic.Level;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.QueryHints;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class QueryDslDbItemReader <T> extends AbstractItemCountingItemStreamItemReader<T> {

    @Getter
    @Setter
    private String name;

    @Setter
    @Getter
    private PlatformTransactionManager transactionManager;

    @Getter
    @Setter
    private EntityManagerFactory entityManagerFactory;

    @Getter
    @Setter
    private JPAQuery query;

    @Getter
    @Setter
    private String dataSourceKey;

    private EntityManager entityManager;

    private Iterator<T> iterator;

    @Builder.Default
    private int readCount = 0;

    @Override
    protected void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [START] QueryDslDbItemReader");
        log.info("| name: {}", name);
        log.info("| query: {}", queryToString(query));
        log.info("{}", StringUtils.repeat("-",80));

        // checks validation
        Assert.notNull(name, "name must not be null");
        Assert.notNull(entityManagerFactory, "entityManagerFactory must not be null");
        Assert.notNull(query, "query must not be null");

        // sets name
        super.setName(name);

        try {
            if(dataSourceKey != null) {
                RoutingDataSource.setKey(dataSourceKey);
            }

            // create query
            ch.qos.logback.classic.Logger sqlLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("org.hibernate.SQL");
            ch.qos.logback.classic.Logger binderLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
            Level sqlLoggerLevel = sqlLogger.getLevel();
            Level binderLoggerLevel = binderLogger.getLevel();
            try {
                sqlLogger.setLevel(Level.DEBUG);
                binderLogger.setLevel(Level.TRACE);
                entityManager = entityManagerFactory.createEntityManager();
                JPAQuery jpaQuery = query.clone(entityManager);
                jpaQuery.setHint(QueryHints.HINT_READONLY, true);
                entityManager.getTransaction().begin();
                Stream stream = jpaQuery.createQuery().getResultStream();
                iterator = stream.iterator();
            }finally{
                sqlLogger.setLevel(sqlLoggerLevel);
                binderLogger.setLevel(binderLoggerLevel);
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
        if(iterator.hasNext()){
            next = iterator.next();
            readCount ++;
        }
        return next;
    }

    @Override
    protected void doClose() throws Exception {
        if(entityManager != null) {
            entityManager.getTransaction().commit();
            entityManager.close();
        }

        // logging
        log.info("{}", StringUtils.repeat("-",80));
        log.info("| [END] QueryDslDbItemReader");
        log.info("| name: {}", name);
        log.info("| query: {}", queryToString(query));
        log.info("| readCount: {}", readCount);
        log.info("{}", StringUtils.repeat("-",80));
    }

    /**
     * query to string
     * @param query
     * @return
     */
    private String queryToString(JPAQuery query){
        String value = query.toString().replaceAll("\n"," ");
        value = StringUtils.abbreviate(value, 80);
        return value;
    }

    /**
     * QueryDslDbItemReaderBuilder
     * @param <T>
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class QueryDslDbItemReaderBuilder<T> {

        private String name;

        private PlatformTransactionManager transactionManager;

        private EntityManagerFactory entityManagerFactory;

        private JPAQuery<?> query;

        private String dataSourceKey;

        /**
         * build
         * @return
         */
        public QueryDslDbItemReader<T> build() {
            QueryDslDbItemReader<T> instance = new QueryDslDbItemReader<T>();
            Optional.ofNullable(name).ifPresent(value -> instance.setName(value));
            Optional.ofNullable(transactionManager).ifPresent(value -> instance.setTransactionManager(value));
            Optional.ofNullable(entityManagerFactory).ifPresent(value -> instance.setEntityManagerFactory(value));
            Optional.ofNullable(query).ifPresent(value -> instance.setQuery(value));
            Optional.ofNullable(dataSourceKey).ifPresent(value -> instance.setDataSourceKey(value));
            return instance;
        }
    }

    /**
     * builder
     * @param <T>
     * @return
     */
    public static <T> QueryDslDbItemReaderBuilder<T> builder() {
        return new QueryDslDbItemReaderBuilder<T>();
    }

}
