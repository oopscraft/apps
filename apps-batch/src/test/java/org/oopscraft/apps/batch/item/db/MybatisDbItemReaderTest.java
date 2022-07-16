package org.oopscraft.apps.batch.item.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.db.vo.DbItemVo;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.springframework.batch.item.ExecutionContext;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
public class MybatisDbItemReaderTest extends AbstractJobTest {

    private final DataSource dataSource;

    private final SqlSessionFactory sqlSessionFactory;

    @Test
    public void test01() throws Exception {

        // creates database reader
        MybatisDbItemReader<DbItemVo> dbItemReader = MybatisDbItemReader.<DbItemVo>builder()
                .name("test")
                .sqlSessionFactory(sqlSessionFactory)
                .dataSource(dataSource)
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
