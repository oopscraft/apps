package org.oopscraft.apps.batch.item.file;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.SimpleBinaryBufferedReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@Slf4j
public class RawFileItemReader extends FlatFileItemReader<byte[]> {

    private String name;

    private String filePath;

    private String encoding = BatchConfig.getEncoding();

    private String lineSeparator = BatchConfig.getLineSeparator();

    private Resource resource;

    private int readCount = 0;

    /**
     * doOpen
     * @throws Exception
     */
    @Override
    protected void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [START] StringFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| encoding: {}", encoding);
        log.info("{}", StringUtils.repeat("-", 80));

        // checks validation
        Assert.notNull(filePath, "FilePath must be defined.");

        // creates resource
        resource = ResourceHandlerFactory.getInstance(filePath).createReadableResource(filePath);

        // resolves file destination
        this.setResource(resource);
        this.setEncoding(encoding);
        this.setBufferedReaderFactory(new SimpleBinaryBufferedReaderFactory());
        this.setLineMapper((line, lineNumber)->{
            return line.getBytes();
        });

        // open
        super.doOpen();
    }

    /**
     * doRead
     * @return
     * @throws Exception
     */
    @Override
    protected byte[] doRead() throws Exception {
        byte[] item = super.doRead();
        if(item != null) {
            readCount ++;
        }
        return item;
    }

    @Override
    protected void doClose() throws Exception {

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
