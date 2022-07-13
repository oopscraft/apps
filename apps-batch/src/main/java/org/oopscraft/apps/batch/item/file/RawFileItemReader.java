package org.oopscraft.apps.batch.item.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.SimpleBinaryBufferedReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
public class RawFileItemReader extends FlatFileItemReader<byte[]> {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String filePath;

    @Getter
    @Setter
    private String encoding = BatchConfig.getEncoding();

    @Getter
    @Setter
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


    /**
     * RawFileItemReaderBuilder
     * @param
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class RawFileItemReaderBuilder {

        private String name;

        private String filePath;

        private String encoding;

        private String lineSeparator;

        /**
         * build
         * @return
         */
        public RawFileItemReader build() {
            RawFileItemReader instance = new RawFileItemReader();
            Optional.ofNullable(name).ifPresent(value -> instance.setName(value));
            Optional.ofNullable(filePath).ifPresent(value -> instance.setFilePath(value));
            Optional.ofNullable(encoding).ifPresent(value -> instance.setEncoding(value));
            Optional.ofNullable(lineSeparator).ifPresent(value -> instance.setLineSeparator(value));
            return instance;
        }
    }

    /**
     * builder
     * @return
     */
    public static RawFileItemReader.RawFileItemReaderBuilder builder() {
        return new RawFileItemReader.RawFileItemReaderBuilder();
    }

}
