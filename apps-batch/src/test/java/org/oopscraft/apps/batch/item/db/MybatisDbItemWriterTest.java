package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.dml.DeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.item.db.dao.DbItemMapper;
import org.oopscraft.apps.batch.item.db.dto.DbItemVo;
import org.oopscraft.apps.batch.item.db.dto.QDbItemEntity;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@RequiredArgsConstructor
public class MybatisDbItemWriterTest extends AbstractJobTest {

    private final PlatformTransactionManager transactionManager;

    private final SqlSessionFactory sqlSessionFactory;

    private final JPAQueryFactory jpaQueryFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteDbItemsAll() {
        DeleteClause deleteClause = jpaQueryFactory.delete(QDbItemEntity.dbItemEntity);
        deleteClause.execute();
    }

    @Test
    @Transactional
    public void testWrite() {

        // delete all db items
        deleteDbItemsAll();

        // creates item writer
        MybatisDbItemWriter<DbItemVo> dbItemWriter = MybatisDbItemWriter.<DbItemVo>builder()
                .name("test")
                .transactionManager(transactionManager)
                .sqlSessionFactory(sqlSessionFactory)
                .mapperClass(DbItemMapper.class)
                .mapperMethod("insertDbItem")
                .build();

        // try
        long writeCount = 1234;
        try {
            dbItemWriter.open(new ExecutionContext());
            for(int i = 0; i < writeCount; i ++ ) {
                DbItemVo dbItemVo = new DbItemVo();
                dbItemVo.setId(String.format("id%d",i));
                dbItemVo.setName(String.format("name%s",i));
                dbItemWriter.write(dbItemVo);
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            dbItemWriter.close();
        }

        // check count
        QDbItemEntity qDbItemEntity = QDbItemEntity.dbItemEntity;
        long selectCount = jpaQueryFactory.select(qDbItemEntity.count()).from(qDbItemEntity).fetchFirst();
        log.info("== selectCount: {}", selectCount);
        assertEquals(selectCount, writeCount);
    }

}

