package org.oopscraft.apps.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Assert;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ProxyModeTestJob
*/
@Slf4j
@Configuration
@ConditionalOnProperty(value = "spring.batch.job.name", havingValue="ProxyModeTestJob")  // 다른 테스트 시 로딩 방지
@BatchComponentScan
public class ProxyModeTestJob extends AbstractJob {

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
