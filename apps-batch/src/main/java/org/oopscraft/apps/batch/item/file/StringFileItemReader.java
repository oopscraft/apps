package org.oopscraft.apps.batch.item.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
public class StringFileItemReader extends FlatFileItemReader<String> {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
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
    protected final void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [OPEN] StringFileItemReader");
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
        log.info("| [CLOSE] StringFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| readCount: {}", readCount);
        log.info("{}", StringUtils.repeat("-", 80));
    }

    /**
     * StringFileItemReaderBuilder
     * @param
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class StringFileItemReaderBuilder {

        private String name;

        private String filePath;

        private String encoding;

        private String lineSeparator;

        /**
         * build
         * @return
         */
        public StringFileItemReader build() {
            StringFileItemReader instance = new StringFileItemReader();
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
    public static StringFileItemReader.StringFileItemReaderBuilder builder() {
        return new StringFileItemReader.StringFileItemReaderBuilder();
    }

}
