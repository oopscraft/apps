package org.oopscraft.apps.batch;

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



@Slf4j
public class BatchApplicationTest {

    /**
     * ProxyModeTestJob
     * Job 에 @Configuration 을 설정 하지 않으면 jobParameter 의 @Value 는 NULL 이다.
     * (장기보험 프로젝트에서는 Light Mode로 동작함.)
     */
    @Slf4j
    @Configuration
    @BatchComponentScan
    @ConditionalOnExpression(value = "${ProxyModeTestJob.enable:false}")       // 다른 테스트 시 로딩 방지
    public static class ProxyModeTestJob extends AbstractJob {

        @Override
        public void initialize(BatchContext batchContext) {
            log.info("== batchContext: {}", batchContext);
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
                Assert.notNull(message, "message is not be null");
                return RepeatStatus.FINISHED;
            };
        }
    }

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
                "--ProxyModeTestJob.enable=true",       // @ConditionalOnExpression enable
                String.format("currentTime=%d", System.currentTimeMillis())
        };
        BatchApplication.main(args);
    }

    /**
     * LightModeTestJob
     */
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

