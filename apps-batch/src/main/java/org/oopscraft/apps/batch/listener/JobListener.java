package org.oopscraft.apps.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Slf4j
public class JobListener implements JobExecutionListener {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        log.info("| startTime: {}", Optional.ofNullable(jobExecution.getStartTime()).map(v->DATE_FORMAT.format(v)).orElse(null));
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
        log.info("| startTime: {}", Optional.ofNullable(jobExecution.getStartTime()).map(v->DATE_FORMAT.format(v)).orElse(null));
        log.info("| endTime: {}", Optional.ofNullable(jobExecution.getEndTime()).map(v->DATE_FORMAT.format(v)).orElse(null));
        log.info("| status: {}", jobExecution.getStatus());
        log.info("| exitStatus: {}", jobExecution.getExitStatus());
        log.info("{}", StringUtils.repeat("=",80));
    }

}
