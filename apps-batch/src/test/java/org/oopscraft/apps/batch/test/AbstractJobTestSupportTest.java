package org.oopscraft.apps.batch.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.LightModeTestJob;
import org.oopscraft.apps.batch.job.ProxyModeTestJob;


/**
 * BatchApplicationTest
 */
@Slf4j
public class AbstractJobTestSupportTest extends AbstractJobTestSupport {

    /**
     * ProxyMode Job 실행 테스트
     * @throws Exception
     */
    @Test
    public void testProxyModeJob() throws Exception {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(ProxyModeTestJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("--spring.batch.job.name", "ProxyModeTestJob")
                .jobParameter("message", "hello")
                .build();
        launchJob(batchContext);
    }

    /**
     * testLightModeJob
     * @throws Exception
     */
    @Test
    public void testLightModeJob() throws Exception {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(LightModeTestJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("message", "hello")
                .build();
        launchJob(batchContext);
    }
}

