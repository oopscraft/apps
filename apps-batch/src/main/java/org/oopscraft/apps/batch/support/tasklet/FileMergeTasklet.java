package org.oopscraft.apps.batch.support.tasklet;

import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.StringFileItemReader;
import org.oopscraft.apps.batch.item.file.StringFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Builder
public class FileMergeTasklet extends AbstractTasklet {

    private static final int COMMIT_INTERVAL = 1000;

    // 입력파일경로
    @Singular("filePathIn")
    private List<String> filePathIns;

    // 출력파일
    private String filePathOut;

    @Builder.Default
    private boolean withHeader = BatchConfig.isWithHeader();

    @Builder.Default
    private String encoding = BatchConfig.getEncoding();

    @Builder.Default
    private String lineSeparator = BatchConfig.getLineSeparator();

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // check parameter validation
        Assert.notNull(filePathIns, "filePathIns must not be null.");
        Assert.notNull(filePathOut, "filePathOut must not be null.");

        // logging
        log.info("{}", StringUtils.repeat("=", 80));
        log.info("| [START] FileMergeTasklet");
        log.info("| filePathIns: {}", filePathIns);
        log.info("| filePathOut: {}", filePathOut);
        log.info("| withHeader: {}", withHeader);
        log.info("| encoding: {}", encoding);
        log.info("{}", StringUtils.repeat("=", 80));

        // file item writer
        StringFileItemWriter fileItemWriter = StringFileItemWriter.builder()
                .name("fileItemWriter")
                .filePath(filePathOut)
                .encoding(encoding)
                .lineSeparator(lineSeparator)
                .build();
        try {
            fileItemWriter.open(executionContext);

            // for loop
            String headerLine = null;
            int readCount = 0;
            int writeCount = 0;
            int partIndex = 0;
            for (String filePathIn : filePathIns) {

                // creates string file reader
                StringFileItemReader fileItemReader = StringFileItemReader.builder()
                        .name("fileItemReader")
                        .filePath(filePathIn)
                        .encoding(encoding)
                        .lineSeparator(lineSeparator)
                        .build();
                try {
                    fileItemReader.open(executionContext);

                    // if AutoHeader enabled
                    if(withHeader){
                        headerLine = fileItemReader.read();

                        // 1st file 인 경우 header line write
                        if(partIndex == 0){
                            fileItemWriter.write(headerLine);
                            commit();
                        }
                    }

                    // read file item
                    for (String line = fileItemReader.read(); line != null; line = fileItemReader.read()) {
                        increaseReadCount();
                        readCount++;
                        fileItemWriter.write(line);
                        increaseWriteCount();
                        commit(COMMIT_INTERVAL);
                        writeCount++;
                    }

                    // increase part index
                    commit();
                    partIndex ++;

                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw e;
                } finally {
                    fileItemReader.close();
                }
            }

            // logging
            log.info("{}", StringUtils.repeat("=", 80));
            log.info("| [END] FileMergeTasklet");
            log.info("| filePathIns: {}", filePathIns);
            log.info("| filePathOut: {}", filePathOut);
            log.info("| withHeader: {}", withHeader);
            log.info("| encoding: {}", encoding);
            log.info("| readCount: {}", readCount);
            log.info("| writeCount: {}", writeCount);
            log.info("{}", StringUtils.repeat("=", 80));

        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }finally{
            fileItemWriter.close();
        }
    }

}