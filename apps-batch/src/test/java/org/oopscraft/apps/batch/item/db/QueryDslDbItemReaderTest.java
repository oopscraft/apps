package org.oopscraft.apps.batch.item.db;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.test.AbstractTaskletTest;
import org.oopscraft.apps.core.user.QUser;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import javax.persistence.EntityManagerFactory;

@Slf4j
public class QueryDslDbItemReaderTest extends AbstractTaskletTest {

    @Test
    public void test() throws Exception {

        // defines query dsl query
        JPAQuery<Tuple> query = new JPAQuery<>();
        QUser qUser = QUser.user;
        query.select(
                qUser.id,
                qUser.name
                )
                .from(qUser)
                .where(qUser.id.eq("admin"));

        // creates query dsl reader
        QueryDslDbItemReader<Tuple> dbItemReader = QueryDslDbItemReader.<Tuple>builder()
                .name("test")
                .entityManagerFactory(getApplicationContext().getBean(EntityManagerFactory.class))
                .query(query)
                .build();

        // try
        try {
            dbItemReader.open(new ExecutionContext());
            for (Tuple tuple = dbItemReader.read(); tuple != null; tuple = dbItemReader.read()) {
                log.info("user.id:{}", tuple.get(qUser.id));
                log.info("user.name:{}", tuple.get(qUser.name));
            }
        }catch(Exception e){
            log.error(e.getMessage(),e);
            throw e;
        }finally{
            dbItemReader.close();
        }
    }
}
