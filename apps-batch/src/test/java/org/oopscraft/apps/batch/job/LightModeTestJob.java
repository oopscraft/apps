package org.oopscraft.apps.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Assert;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * LightModeTestJob
 */
@Slf4j
@BatchComponentScan
public class LightModeTestJob extends AbstractJob {

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
