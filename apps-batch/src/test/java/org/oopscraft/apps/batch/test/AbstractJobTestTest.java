package org.oopscraft.apps.batch.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.LightModeTestJob;
import org.oopscraft.apps.batch.job.ProxyModeTestJob;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class AbstractJobTestTest extends AbstractJobTest {

    @Test
    public void testProxyModeTestJob() throws Exception {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(ProxyModeTestJob.class)
                .baseDate("20110101")
                .jobParameter("message", "hello")
                .build();
        JobExecution jobExecution = runJob(batchContext);
        assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
    }

    @Test
    public void testLightModeTestJob() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(LightModeTestJob.class)
                .baseDate("20110101")
                .jobParameter("message", "hello")
                .build();
        JobExecution jobExecution = runJob(batchContext);
        assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
    }

}
