package org.oopscraft.apps.batch.item.file;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@Slf4j
@Builder
public class StringFileItemReader extends FlatFileItemReader<String> {

    private String name;

    private String filePath;

    @Builder.Default
    private String encoding = BatchConfig.getEncoding();

    @Builder.Default
    private String lineSeparator = BatchConfig.getLineSeparator();

    private Resource resource;

    @Builder.Default
    private int readCount = 0;

    /**
     * doOpen
     * @throws Exception
     */
    @Override
    protected final void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [START] StringFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("{}", StringUtils.repeat("-", 80));

        // checks validation
        Assert.notNull(filePath, "FilePath must be defined.");

        // creates resource
        resource = ResourceHandlerFactory.getInstance(filePath).createReadableResource(filePath);

        // line separator policy
        DefaultRecordSeparatorPolicy recordSeparatorPolicy = new DefaultRecordSeparatorPolicy();
        recordSeparatorPolicy.isEndOfRecord(lineSeparator);

        // resolves file destination
        this.setEncoding(encoding);
        this.setResource(resource);
        this.setRecordSeparatorPolicy(recordSeparatorPolicy);
        this.setLineMapper((line, lineNumber) -> {
                return line;
        });

        // open
        super.doOpen();
    }

    @Override
    protected String doRead() throws Exception {
        String item = super.doRead();
        if(item != null) {
            readCount ++;
        }
        return item;
    }

    @Override
    protected final void doClose() throws Exception {
        // close
        super.doClose();

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [END] StringFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| readCount: {}", readCount);
        log.info("{}", StringUtils.repeat("-", 80));
    }

}
