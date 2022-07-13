package org.oopscraft.apps.batch.item.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StringFileItemWriter extends FlatFileItemWriter<String> {

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

    private int writeCount = 0;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [START] StringFileItemWriter");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("{}", StringUtils.repeat("-", 80));

        // checks validation
        Assert.notNull(filePath, "FilePath must be defined.");

        // creates local resource
        resource = ResourceHandlerFactory.getInstance(filePath).createWritableResource(filePath);

        // creates flat file item writer.
        this.setTransactional(true);
        this.setEncoding(encoding);
        this.setLineSeparator(lineSeparator);
        this.setResource(resource);
        this.setLineAggregator(new PassThroughLineAggregator<>());

        // opens writer
        super.open(executionContext);
    }

    /**
     * write
     * @param items
     * @throws Exception
     */
    @Override
    public void write(List<? extends String> items) throws Exception {
        for(String item : items){
            write(item);
        }
    }

    /**
     * write
     * @param item
     * @throws Exception
     */
    public void write(String item) throws Exception {
        super.write(Arrays.asList(item));
        writeCount ++;
    }

    @Override
    public void close() {

        // close
        super.close();

        // put resource
        ResourceHandlerFactory.getInstance(filePath).flushWritableResource(resource, filePath);

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [END] StringFileItemWriter");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| writeCount: {}", writeCount);
        log.info("{}", StringUtils.repeat("-", 80));
    }

    /**
     * StringFileItemWriterBuilder
     * @param
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class StringFileItemWriterBuilder {

        private String name;

        private String filePath;

        private String encoding;

        private String lineSeparator;

        /**
         * build
         * @return
         */
        public StringFileItemWriter build() {
            StringFileItemWriter instance = new StringFileItemWriter();
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
    public static StringFileItemWriter.StringFileItemWriterBuilder builder() {
        return new StringFileItemWriter.StringFileItemWriterBuilder();
    }


}
