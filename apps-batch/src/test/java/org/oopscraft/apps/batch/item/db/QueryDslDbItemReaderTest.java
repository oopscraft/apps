package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.db.dto.DbItemEntity;
import org.oopscraft.apps.batch.item.db.dto.QDbItemEntity;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.oopscraft.apps.core.property.QProperty;
import org.oopscraft.apps.core.user.QUser;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class QueryDslDbItemReaderTest extends AbstractJobTest {

    private final EntityManagerFactory entityManagerFactory;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * create test data
     * @param insertCount
     */
    public void createDbItemForTest(long insertCount) {
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

        // create test data
        long insertCount = 1234;
        createDbItemForTest(insertCount);

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
}
