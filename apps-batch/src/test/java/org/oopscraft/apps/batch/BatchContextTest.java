package org.oopscraft.apps.batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.springframework.batch.core.Job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BatchContextTest {

    @Test
    public void test() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(Job.class)
                .baseDate("20110101")
                .jobParameter("key01", "value01")
                .jobParameter("key02", "value02")
                .build();
        log.info("== batchContext:{}", batchContext);
        assertEquals(batchContext.getJobParameter("key01"), "value01");
        batchContext.setJobParameter("key03", "value03");
        log.info("== batchContext:{}", batchContext);
        assertEquals(batchContext.getJobParameter("key03"), "value03");
    }

}
