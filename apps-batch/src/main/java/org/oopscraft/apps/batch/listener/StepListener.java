package org.oopscraft.apps.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import java.util.Date;
import java.util.Optional;

@Slf4j
public class StepListener implements StepExecutionListener, ChunkListener {

    public static final int LOGGING_CHUNK_INTERVAL = 10;

    public int chunkCount = 0;

    @Override
    public final void beforeStep(StepExecution stepExecution) {
        log.info("{}", StringUtils.repeat("─",80));
        log.info("| [START] StepExecution");
        log.info("| stepName: {}", stepExecution.getStepName());
        log.info("| startTime: {}", stepExecution.getStartTime());
        log.info("{}", StringUtils.repeat("─",80));
    }

    @Override
    public void beforeChunk(ChunkContext chunkContext) {
        log.debug("beforeChunk[{}]", chunkContext);
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        chunkCount ++;
        log.debug("afterChunk[{}]", chunkContext);
        if(chunkCount%LOGGING_CHUNK_INTERVAL == 0) {
            loggingChunkContext(chunkContext);
        }
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {
        log.warn("chunkError[{}]", chunkContext);
    }

    @Override
    public final ExitStatus afterStep(StepExecution stepExecution) {
        loggingChunkContext(stepExecution);
        stepExecution.incrementCommitCount();       // apply last commit
        log.info("{}", StringUtils.repeat("─",80));
        log.info("| [END] StepExecution");
        log.info("| stepName: {}", stepExecution.getStepName());
        log.info("| startTime: {}", stepExecution.getStartTime());
        log.info("| endTime: {}", Optional.ofNullable(stepExecution.getEndTime()).orElse(new Date()));
        log.info("| readCount: {}", stepExecution.getReadCount());
        log.info("| filterCount: {}", stepExecution.getFilterCount());
        log.info("| writeCount: {}", stepExecution.getWriteCount());
        log.info("| commitCount: {}", stepExecution.getCommitCount());
        log.info("| rollbackCount: {}", stepExecution.getRollbackCount());
        log.info("| exitStatus: {}", stepExecution.getExitStatus());
        log.info("| failureExceptions: {}", stepExecution.getFailureExceptions());
        log.info("{}", StringUtils.repeat("─",80));
        return stepExecution.getExitStatus();
    }

    /**
     * loggingChunkContext
     * @param chunkContext
     */
    private void loggingChunkContext(ChunkContext chunkContext) {
        StepContext stepContext = chunkContext.getStepContext();
        StepExecution stepExecution = stepContext.getStepExecution();
        loggingChunkContext(stepExecution);
    }

    /**
     * loggingChunkContext
     * @param stepExecution
     */
    private void loggingChunkContext(StepExecution stepExecution) {
        log.info("stepName={}, readCount={}, filterCount={}, writeCount={}, commitCount={}"
                ,stepExecution.getStepName()
                ,stepExecution.getReadCount()
                ,stepExecution.getFilterCount()
                ,stepExecution.getWriteCount()
                ,stepExecution.getCommitCount()
        );
    }

}
