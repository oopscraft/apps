package org.oopscraft.apps.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

public class BatchApplicationTest {

    @Slf4j
    @RequiredArgsConstructor
    public static class TestJob extends AbstractJob {

        @Override
        public void initialize() {
            log.info("++++++++++++++++++ test +++++++++++++++++++++");
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
                if(1 == 1) {
                    throw new RuntimeException("TEST");
                }
                return RepeatStatus.FINISHED;
            };
        }
    }


    @Test
    public void test() throws Exception {
        BatchApplication.main(new String[]{
                TestJob.class.getName(),
                "20110101",
                "message=hello"
        });
    }

    @Test
    public void testCheckFail() throws Exception {
        BatchApplication.main(new String[]{
                TestJob.class.getName(),
                "20110101",
                "test=val"
        });
    }
}
