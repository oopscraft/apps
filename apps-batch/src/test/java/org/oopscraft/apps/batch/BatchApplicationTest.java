package org.oopscraft.apps.batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.job.LightModeTestJob;
import org.oopscraft.apps.batch.job.ProxyModeTestJob;


/**
 * BatchApplicationTest
 */
@Slf4j
public class BatchApplicationTest {

    /**
     * ProxyMode Job 실행 테스트
     * @throws Exception
     */
    @Test
    public void testProxyModeJob() throws Exception {
        String[] args = new String[]{
                ProxyModeTestJob.class.getName(),
                "20110101",
                "message=hello",
                "--org.oopscraft.apps.batch.BatchApplicationTest.ProxyModeTestJob.enable=true", // @ConditionalOnExpression enable
                String.format("currentTime=%d", System.currentTimeMillis())
        };
        BatchApplication.main(args);
    }

    /**
     * testLightModeJob
     * @throws Exception
     */
    @Test
    public void testLightModeJob() throws Exception {
        String[] args = new String[]{
                LightModeTestJob.class.getName(),
                "20110101",
                "message=hello",
                String.format("currentTime=%d", System.currentTimeMillis())
        };
        BatchApplication.main(args);
    }
}

