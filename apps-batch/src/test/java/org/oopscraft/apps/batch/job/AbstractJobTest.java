package org.oopscraft.apps.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchApplication;
import org.oopscraft.apps.batch.context.BatchContext;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

public class AbstractJobTest {

    @Slf4j
    @RequiredArgsConstructor
    public static class TestJob extends AbstractJob {

        @Override
        public void initialize(BatchContext batchContext) {
            log.info("== initialize: {}", batchContext);
            addStep(step());
        }

        @Bean
        @JobScope
        public Step step() {
            return getStepBuilderFactory().get("testStep01")
                    .tasklet(tasklet(null))
                    .build();
        }

        @Bean
        @StepScope
        public Tasklet tasklet(@Value("#{jobParameters[message]}")String message) {
            return (contribution, chunkContext) -> {
                log.info("== message: {}", message);
                return RepeatStatus.FINISHED;
            };
        }
    }

    @Test
    public void test() throws Exception {
        BatchApplication.main(new String[]{
                AbstractJobTest.TestJob.class.getName(),
                "20110101",
                "message=hello",
                String.format("uuid=%s", UUID.randomUUID().toString())
        });
    }


}
