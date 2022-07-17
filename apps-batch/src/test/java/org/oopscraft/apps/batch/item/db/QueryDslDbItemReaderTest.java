package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.oopscraft.apps.batch.item.db.dao.DbItemMapper;
import org.oopscraft.apps.batch.item.db.dto.*;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class QueryDslDbItemReaderTest extends AbstractJobTest {

    /**
     * delete db item backup all
     */
    public void clearTestData() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        jpaQueryFactory.delete(QDbItemEntity.dbItemEntity).execute();
        jpaQueryFactory.delete(QDbItemBackupEntity.dbItemBackupEntity).execute();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * create test data
     * @param insertCount
     */
    public void createTestData(long insertCount) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        for(int i = 0; i < insertCount; i++ ) {
            DbItemEntity dbItemEntity = DbItemEntity.builder()
                    .id(String.format("id-%d",i))
                    .name(String.format("name-%d",i))
                    .build();
            entityManager.persist(dbItemEntity);
            entityManager.flush();
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }


    @Test
    public void testRead() throws Exception {

        // initialize
        long insertCount = 1234;
        clearTestData();
        createTestData(insertCount);

        // defines query
        JPAQuery query = jpaQueryFactory.query();
        QDbItemEntity qDbItemEntity = QDbItemEntity.dbItemEntity;
        query.select(qDbItemEntity.id, qDbItemEntity.name)
                .from(qDbItemEntity);

        // creates query dsl reader
        QueryDslDbItemReader<Tuple> dbItemReader = QueryDslDbItemReader.<Tuple>builder()
                .name("test")
                .entityManagerFactory(entityManagerFactory)
                .query(query)
                .build();

        // try
        long readCount = 0;
        try {
            dbItemReader.open(new ExecutionContext());
            for (Tuple tuple = dbItemReader.read(); tuple != null; tuple = dbItemReader.read()) {
                readCount ++;
                log.info("user.id:{}", tuple.get(qDbItemEntity.id));
                log.info("user.name:{}", tuple.get(qDbItemEntity.name));
            }
        }catch(Exception e){
            log.error(e.getMessage(),e);
            throw e;
        }finally{
            dbItemReader.close();
        }

        // check count
        long count = jpaQueryFactory.select(qDbItemEntity.count()).from(qDbItemEntity).fetchFirst();
        log.info("== count:{}", count);

        // checks
        assertEquals(count, insertCount);
        assertEquals(readCount, insertCount);
    }

    @Test
    @Transactional
    public void testReadWithMybatisWriter() throws Exception {

        // initialize
        long insertCount = 1234;
        clearTestData();
        createTestData(insertCount);

        // creates reader
        JPAQuery query = jpaQueryFactory.query();
        QDbItemEntity qDbItemEntity = QDbItemEntity.dbItemEntity;
        query.select(qDbItemEntity)
                .from(qDbItemEntity);
        QueryDslDbItemReader<DbItemEntity> dbItemReader = QueryDslDbItemReader.<DbItemEntity>builder()
                .name("dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .query(query)
                .build();

        // creates writer
        MybatisDbItemWriter<DbItemBackupVo> dbItemBackupWriter = MybatisDbItemWriter.<DbItemBackupVo>builder()
                .name("dbItemBackupWriter")
                .transactionManager(transactionManager)
                .sqlSessionFactory(sqlSessionFactory)
                .mapperClass(DbItemMapper.class)
                .mapperMethod("insertDbItemBackup")
                .build();

        // try
        long readCount = 0;
        long writeCount = 0;
        ExecutionContext executionContext = new ExecutionContext();
        try {
            dbItemReader.open(executionContext);
            dbItemBackupWriter.open(executionContext);
            for(DbItemEntity item = dbItemReader.read(); item != null; item = dbItemReader.read()) {
                readCount ++;
                log.info("item:{}", item);
                DbItemBackupVo itemBackup = DbItemBackupVo.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build();
                dbItemBackupWriter.write(itemBackup);
                writeCount ++;
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            log.info("executionContext:{}", executionContext);
            dbItemBackupWriter.close();
            dbItemReader.close();
        }

        // check read/write count
        Assert.assertEquals(readCount, insertCount);
        Assert.assertEquals(writeCount, insertCount);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = jpaQueryFactory
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, insertCount);
    }

    /**
     * Mybatis Reader -> JPA Writer
     * @throws Exception
     */
    @Test
    @Transactional
    public void testReadWithJpaWriter() throws Exception {

        // initialize
        long insertCount = 1234;
        clearTestData();
        createTestData(insertCount);

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("dbItemReader")
                .sqlSessionFactory(sqlSessionFactory)
                .dataSource(dataSource)
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", insertCount)
                .build();

        // creates item writer
        JpaDbItemWriter<DbItemBackupEntity> dbItemBackupWriter = JpaDbItemWriter.<DbItemBackupEntity>builder()
                .name("dbItemBackupWriter")
                .entityManagerFactory(entityManagerFactory)
                .transactionManager(transactionManager)
                .build();

        // try
        long readCount = 0;
        long writeCount = 0;
        ExecutionContext executionContext = new ExecutionContext();
        try {
            dbItemReader.open(executionContext);
            dbItemBackupWriter.open(executionContext);
            for(DbItemVo item = dbItemReader.read(); item != null; item = dbItemReader.read()) {
                readCount ++;
                log.info("item:{}", item);
                DbItemBackupEntity itemBackup = DbItemBackupEntity.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build();
                dbItemBackupWriter.write(itemBackup);
                writeCount ++;
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            log.info("executionContext:{}", executionContext);
            dbItemBackupWriter.close();
            dbItemReader.close();
        }

        // check read/write count
        Assert.assertEquals(readCount, insertCount);
        Assert.assertEquals(writeCount, insertCount);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = jpaQueryFactory
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, insertCount);

    }

}
