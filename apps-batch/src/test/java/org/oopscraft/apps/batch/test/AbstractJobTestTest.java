package org.oopscraft.apps.batch.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.modelmapper.internal.util.Assert;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@ContextConfiguration(classes = {
        AbstractJobTestTest.LightModeTestJob.class
})
public class AbstractJobTestTest extends AbstractJobTest {

    @BatchComponentScan
    public static class LightModeTestJob extends AbstractJob {

        @Override
        public void initialize(BatchContext batchContext) {
            log.info("== batchContext: {}", batchContext);
            String message = batchContext.getJobParameter("message");
            addStep(step(message));
        }

        public Step step(String message) {
            return getStepBuilderFactory().get("testStep01")
                    .tasklet(tasklet(message))
                    .build();
        }

        public Tasklet tasklet(String message) {
            return (contribution, chunkContext) -> {
                log.info("== message: {}", message);
                Assert.notNull(message, "message is not be null");
                return RepeatStatus.FINISHED;
            };
        }
    }

    @Test
    public void testProxyModeTestJob() {

    }

}
