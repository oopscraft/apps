package org.oopscraft.apps.batch.support.tasklet;


import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.StringFileItemReader;
import org.oopscraft.apps.batch.item.file.StringFileItemWriter;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

@Slf4j
@Builder
public class FileSplitTasklet extends AbstractTasklet {

    private static final int COMMIT_INTERVAL = 1000;

    // 입력파일경로
    private String filePathIn;

    // 출력파일(Prefix)
    private String filePathOut;

    // 분할건수
    @Builder.Default
    private int splitSize = 10;

    @Builder.Default
    private boolean withHeader = BatchConfig.isWithHeader();

    @Builder.Default
    private String encoding = BatchConfig.getEncoding();

    @Builder.Default
    private String lineSeparator = BatchConfig.getLineSeparator();

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // check parameter validation
        Assert.notNull(filePathIn, "filePathIn must not be null.");
        Assert.notNull(filePathOut, "filePathOut must not be null.");
        Assert.isTrue(splitSize > 0, "splitSize must be over zero.");

        // logging
        log.info("{}", StringUtils.repeat("=", 80));
        log.info("| [START] FileSplitTasklet");
        log.info("| filePathIn: {}", filePathIn);
        log.info("| splitSize: {}", splitSize);
        log.info("| filePathOut: {}", filePathOut);
        log.info("| withHeader: {}", withHeader);
        log.info("| encoding: {}", encoding);
        log.info("{}", StringUtils.repeat("=", 80));

        // creates string file reader
        StringFileItemReader fileItemReader = StringFileItemReader.builder()
                .name("fileItemReader")
                .filePath(filePathIn)
                .encoding(encoding)
                .lineSeparator(lineSeparator)
                .build();
        long totalItemCount = ResourceHandlerFactory.getInstance(filePathIn).getLineCount(filePathIn, lineSeparator) - (withHeader ? 1 : 0);
        long linePerFile =  (long)Math.ceil((double)totalItemCount/splitSize);
        int partIndex = 0;
        int readCount = 0;
        int writeCount = 0;
        try {
            // open reader
            fileItemReader.open(executionContext);

            // creates file item writer first
            StringFileItemWriter fileItemWriter = createPartFileItemWriter(partIndex);
            fileItemWriter.open(executionContext);

            // variables
            String headerLine = null;

            // autoHeader
            if(withHeader){
                headerLine = fileItemReader.read();
                fileItemWriter.write(headerLine);
                commit();
            }

            // fetch record
            boolean isNewFile = false;
            for(String line = fileItemReader.read(); line != null; line = fileItemReader.read()){

                // check new file
                if(isNewFile){
                    log.info("start partIndex[{}]", partIndex);
                    partIndex ++;
                    fileItemWriter = createPartFileItemWriter(partIndex);
                    fileItemWriter.open(executionContext);
                    if(headerLine != null) {
                        fileItemWriter.write(headerLine);
                        commit();
                    }
                }

                // increase read count
                increaseReadCount();
                readCount ++;
                log.trace("line:{}", line);

                // write line
                fileItemWriter.write(line);
                increaseWriteCount();
                commit(COMMIT_INTERVAL);
                writeCount ++;

                // check flush
                if(readCount%linePerFile == 0){
                    // close file
                    commit();
                    fileItemWriter.close();
                    log.info("end partIndex[{}]", partIndex);
                    isNewFile = true;
                }else{
                    isNewFile = false;
                }
            }

            // close final file
            commit();
            fileItemWriter.close();

            // create dummy part file
            if(partIndex < splitSize-1){
                for(int i = partIndex+1; i < splitSize; i ++) {
                    fileItemWriter = createPartFileItemWriter(i);
                    fileItemWriter.open(executionContext);
                    if(headerLine != null){
                        fileItemWriter.write(headerLine);
                    }
                    fileItemWriter.close();
                    commit();
                    partIndex ++;
                }
            }

            // check split size and part size
            Assert.isTrue(splitSize == (partIndex+1), String.format("partSize[%d] is mismatched splitSize[%d]", (partIndex+1), splitSize));

        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw e;
        }finally{
            fileItemReader.close();
        }

        // logging
        log.info("{}", StringUtils.repeat("=", 80));
        log.info("| [END] FileSplitTasklet");
        log.info("| filePathIn: {}", filePathIn);
        log.info("| splitSize: {}", splitSize);
        log.info("| filePathOut: {}", filePathOut);
        log.info("| withHeader: {}", withHeader);
        log.info("| encoding: {}", encoding);
        log.info("| readCount: {}", readCount);
        log.info("| writeCount: {}", writeCount);
        log.info("| partSize: {}", partIndex + 1);
        log.info("{}", StringUtils.repeat("=", 80));
    }

    /**
     * createPartFileItemWriter
     * @param partIndex
     * @return
     */
    private StringFileItemWriter createPartFileItemWriter(int partIndex) {
        StringFileItemWriter partFileItemWriter = StringFileItemWriter.builder()
                .name("fileItemWriter")
                .filePath(filePathOut + "." + partIndex)
                .encoding(encoding)
                .lineSeparator(lineSeparator)
                .build();
        return partFileItemWriter;
    }

}