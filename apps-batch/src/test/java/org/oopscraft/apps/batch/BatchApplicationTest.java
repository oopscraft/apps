package org.oopscraft.apps.batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.modelmapper.internal.util.Assert;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.batch.test.job.LightModeTestJob;
import org.oopscraft.apps.batch.test.job.ProxyModeTestJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * BatchApplicationTest
 */
@Slf4j
public class BatchApplicationTest {

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
                "--org.oopscraft.apps.batch.BatchApplicationTest.ProxyModeTestJob.enable=true", // @ConditionalOnExpression enable
                String.format("currentTime=%d", System.currentTimeMillis())
        };
        BatchApplication.main(args);
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

