package org.oopscraft.apps.batch.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.modelmapper.internal.util.Assert;
import org.oopscraft.apps.batch.BatchConfiguration;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * BatchApplicationTest
 */
@Slf4j
public class AbstractJobTestSupportTest extends AbstractJobTestSupport {

    /**
     * ProxyModeTestJob
     * Job 에 @Configuration 을 설정 하지 않으면 jobParameter 의 @Value 는 NULL 이다.
     * (장기보험 프로젝트에서는 Light Mode로 동작함.)
     */
    @Slf4j
    @Configuration
    @BatchComponentScan
    @ConditionalOnExpression(value = "${org.oopscraft.apps.batch.test.AbstractJobTestSupportTest.ProxyModeTestJob:false}")       // 다른 테스트 시 로딩 방지
    protected static class ProxyModeTestJob extends AbstractJob {

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
        BatchContext batchContext = BatchContext.builder()
                .jobClass(ProxyModeTestJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("message", "hello")
                .jobParameter("--org.oopscraft.apps.batch.test.AbstractJobTestSupportTest.ProxyModeTestJob", "true")
                .build();
        launchJob(batchContext);
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
        BatchContext batchContext = BatchContext.builder()
                .jobClass(LightModeTestJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("message", "hello")
                .build();
        launchJob(batchContext);
    }
}

