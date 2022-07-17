package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.oopscraft.apps.batch.item.db.dao.DbItemMapper;
import org.oopscraft.apps.batch.item.db.dto.*;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class MybatisDbItemReaderTest extends AbstractJobTest {

    /**
     * deleteDbItemBackupAll
     */
    public void deleteDbItemBackupAll() {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        DeleteClause deleteClause = new JPAQueryFactory(entityManager)
                .delete(QDbItemBackupEntity.dbItemBackupEntity);
        deleteClause.execute();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * Mybatis Reader
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {

        // test count
        long count = 1234;

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("test")
                .sqlSessionFactory(getSqlSessionFactory())
                .dataSource(getDataSource())
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", count)
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
        assertEquals(readCount, count);
    }

    /**
     * Mybatis Reader -> Mybatis Writer
     * @throws Exception
     */
    @Test
    @Transactional
    public void testReadWithMybatisWriter() throws Exception {

        // test count
        long count = 1234;

        // delete db item backup
        deleteDbItemBackupAll();

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("dbItemReader")
                .sqlSessionFactory(getSqlSessionFactory())
                .dataSource(getDataSource())
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", count)
                .build();

        // creates item writer
        MybatisDbItemWriter<DbItemVo> dbItemBackupWriter = MybatisDbItemWriter.<DbItemVo>builder()
                .name("dbItemBackupWriter")
                .transactionManager(getTransactionManager())
                .sqlSessionFactory(getSqlSessionFactory())
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
        assertEquals(readCount, count);
        assertEquals(writeCount, count);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = getJpaQueryFactory()
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, count);
    }

    /**
     * Mybatis Reader -> JPA Writer
     * @throws Exception
     */
    @Test
    @Transactional
    public void testReadWithJpaWriter() throws Exception {

        // test count
        long count = 1234;

        // delete db item backup
        deleteDbItemBackupAll();

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("dbItemReader")
                .sqlSessionFactory(getSqlSessionFactory())
                .dataSource(getDataSource())
                .mapperClass(DbItemMapper.class)
                .mapperMethod("selectDbItems")
                .parameter("limit", count)
                .build();

        // creates item writer
        JpaDbItemWriter<DbItemBackupEntity> dbItemBackupWriter = JpaDbItemWriter.<DbItemBackupEntity>builder()
                .name("dbItemBackupWriter")
                .entityManagerFactory(getEntityManagerFactory())
                .transactionManager(getTransactionManager())
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
        assertEquals(readCount, count);
        assertEquals(writeCount, count);

        // check select count
        QDbItemBackupEntity qDbItemBackupEntity = QDbItemBackupEntity.dbItemBackupEntity;
        long dbItemBackupCount = getJpaQueryFactory()
                .select(qDbItemBackupEntity.count())
                .from(qDbItemBackupEntity)
                .fetchFirst();
        log.info("== dbItemBackupCount: {}", dbItemBackupCount);
        Assertions.assertEquals(dbItemBackupCount, count);

    }

}
