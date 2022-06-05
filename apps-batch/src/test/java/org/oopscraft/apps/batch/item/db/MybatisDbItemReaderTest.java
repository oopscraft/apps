package org.oopscraft.apps.batch.item.db;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.db.vo.DbItemVo;
import org.oopscraft.apps.batch.test.AbstractTaskletTest;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import javax.sql.DataSource;

@Slf4j
public class MybatisDbItemReaderTest extends AbstractTaskletTest {

    @Test
    public void test01() throws Exception {

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("test")
                .sqlSessionFactory(getApplicationContext().getBean(SqlSessionFactory.class))
                .dataSource(getApplicationContext().getBean(DataSource.class))
                .mapperClass(MybatisDbItemReaderTestMapper.class)
                .mapperMethod("selectItems")
                .parameter("limit", 1234)
                .build();

        // try
        ExecutionContext executionContext = new ExecutionContext();
        try {
            dbItemReader.open(executionContext);
            for(DbItemVo item = dbItemReader.read(); item != null; item = dbItemReader.read()) {
                log.info("item:{}", item);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            log.info("executionContext:{}", executionContext);
            dbItemReader.close();
        }

    }


}
