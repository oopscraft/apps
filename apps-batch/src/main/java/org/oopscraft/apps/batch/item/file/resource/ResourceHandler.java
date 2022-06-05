package org.oopscraft.apps.batch.item.file.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.Resource;

@Slf4j
public abstract class ResourceHandler {

    public abstract boolean supports(String filePath);

    public abstract Resource createReadableResource(String filePath);

    public abstract Resource createWritableResource(String filePath);

    public abstract void flushWritableResource(Resource resource, String filePath);

    public long getLineCount(String filepath, String lineSeparator) {
        long lineCount = 0;
        try {
            FlatFileItemReader<String> fileItemReader = new FlatFileItemReader<>();
            fileItemReader.setResource(createReadableResource(filepath));
            DefaultRecordSeparatorPolicy recordSeparatorPolicy = new DefaultRecordSeparatorPolicy();
            recordSeparatorPolicy.isEndOfRecord(lineSeparator);
            fileItemReader.setRecordSeparatorPolicy(recordSeparatorPolicy);
            fileItemReader.setLineMapper((line, lineNumber) -> line);
            fileItemReader.open(new ExecutionContext());
            while (fileItemReader.read() != null) {
                lineCount++;
            }
        } catch (Exception e) {
            log.error("getLineCount error", e);
            throw new RuntimeException(e);
        }
        return lineCount;
    }



}
