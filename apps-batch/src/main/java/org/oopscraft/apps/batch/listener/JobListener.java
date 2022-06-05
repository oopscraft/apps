package org.oopscraft.apps.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobListener implements JobExecutionListener {

    /**
     * beforeJob
     * @param jobExecution
     */
    @Override
    public final void beforeJob(JobExecution jobExecution) {
        log.info("{}", StringUtils.repeat("=",80));
        log.info("| [START] JobExecution");
        log.info("| jobName: {}", jobExecution.getJobInstance().getJobName());
        log.info("| jobParameters: {}", jobExecution.getJobParameters());
        log.info("| startTime: {}", jobExecution.getStartTime());
        log.info("| status: {}", jobExecution.getStatus());
        log.info("{}", StringUtils.repeat("=",80));
    }

    /**
     * afterJob
     * @param jobExecution
     */
    @Override
    public final void afterJob(JobExecution jobExecution) {
        log.info("{}", StringUtils.repeat("=",80));
        log.info("| [END] JobExecution");
        log.info("| jobName: {}", jobExecution.getJobInstance().getJobName());
        log.info("| jobParameters: {}", jobExecution.getJobParameters());
        log.info("| startTime: {}", jobExecution.getStartTime());
        log.info("| endTime: {}", jobExecution.getEndTime());
        log.info("| status: {}", jobExecution.getStatus());
        log.info("| exitStatus: {}", jobExecution.getExitStatus());
        log.info("| failureExceptions: {}", jobExecution.getFailureExceptions());
        log.info("{}", StringUtils.repeat("=",80));
    }

}
