package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.item.db.dao.DbItemMapper;
import org.oopscraft.apps.batch.item.db.dto.*;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class MybatisDbItemReaderTest extends AbstractJobTest {

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

    /**
     * Mybatis Reader
     * @throws Exception
     */
    @Test
    @Order(1)
    public void testRead() throws Exception {

        // initialize
        long testCount = 1234;
        clearTestData();
        createTestData(testCount);

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("test")
                .sqlSessionFactory(getSqlSessionFactory())
                .dataSource(getDataSource())
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", testCount)
                .build();

        // try
        long readCount = 0;
        ExecutionContext executionContext = new ExecutionContext();
        try {
            dbItemReader.open(executionContext);
            for(DbItemVo item = dbItemReader.read(); item != null; item = dbItemReader.read()) {
                readCount ++;
                log.info("item:{}", item);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            log.info("executionContext:{}", executionContext);
            dbItemReader.close();
        }

        // check read count
        assertEquals(readCount, testCount);
    }

    /**
     * Mybatis Reader -> Mybatis Writer
     * @throws Exception
     */
    @Test
    @Order(2)
    @Transactional
    public void testReadWithMybatisWriter() throws Exception {

        // initialize
        long testCount = 1234;
        clearTestData();
        createTestData(testCount);

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("dbItemReader")
                .sqlSessionFactory(sqlSessionFactory)
                .dataSource(dataSource)
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", testCount)
                .build();

        // creates item writer
        MybatisDbItemWriter<DbItemVo> dbItemBackupWriter = MybatisDbItemWriter.<DbItemVo>builder()
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
            for(DbItemVo item = dbItemReader.read(); item != null; item = dbItemReader.read()) {
                readCount ++;
                log.info("item:{}", item);
                DbItemBackupVo itemBackup = DbItemBackupVo.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build();
                dbItemBackupWriter.write(item);
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
        assertEquals(readCount, testCount);
        assertEquals(writeCount, testCount);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = jpaQueryFactory
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, testCount);
    }

    /**
     * Mybatis Reader -> JPA Writer
     * @throws Exception
     */
    @Test
    @Order(3)
    @Transactional
    public void testReadWithJpaWriter() throws Exception {

        // initialize
        long testCount = 1234;
        clearTestData();
        createTestData(testCount);

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("dbItemReader")
                .sqlSessionFactory(sqlSessionFactory)
                .dataSource(dataSource)
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", testCount)
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
        assertEquals(readCount, testCount);
        assertEquals(writeCount, testCount);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = jpaQueryFactory
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, testCount);

    }

}
