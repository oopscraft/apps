package org.oopscraft.apps.batch.item.file;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.WriterNotOpenException;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public abstract class GenericFileItemWriter<T> extends FlatFileItemWriter<T> {

    @Setter
    @Getter
    protected String name;

    @Setter
    @Getter
    protected String filePath;

    @Setter
    @Getter
    protected Class<?> itemType;

    @Setter
    @Getter
    protected boolean withHeader = BatchConfig.isWithHeader();

    @Setter
    @Getter
    protected String encoding = BatchConfig.getEncoding();

    @Setter
    @Getter
    protected String lineSeparator = BatchConfig.getLineSeparator();

    @Setter
    @Getter
    protected String dateFormat = BatchConfig.getDateFormat();

    @Setter
    @Getter
    protected String dateTimeFormat = BatchConfig.getDateTimeFormat();

    @Setter
    @Getter
    protected String timestampFormat = BatchConfig.getTimestampFormat();

    protected FlatFileHeaderCallback headerCallback;

    protected FlatFileFooterCallback footerCallback;

    protected Resource resource;

    private HashMap<Class<?>, LineAggregator> lineAggregatorRegistry = new LinkedHashMap<>();

    /**
     * Defines custom header
     * @param headerCallback headerCallback
     */
    @Override
    public final void setHeaderCallback(FlatFileHeaderCallback headerCallback) {
        super.setHeaderCallback(headerCallback);
        this.headerCallback = headerCallback;
    }

    /**
     * Defines custom footer
     * @param footerCallback footerCallback
     */
    @Override
    public final void setFooterCallback(FlatFileFooterCallback footerCallback) {
        super.setFooterCallback(footerCallback);
        this.footerCallback = footerCallback;
    }

    @Override
    public final void open(ExecutionContext executionContext) throws ItemStreamException {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [START] {}}", this.getClass().getSimpleName());
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| itemType: {}", itemType);
        log.info("{}", StringUtils.repeat("-",80));

        // check validation
        Assert.notNull(name, "name must be defined");
        Assert.notNull(filePath, "filePath must be defined");
        Assert.notNull(itemType, "itemType must be defined");

        // create resource
        resource = ResourceHandlerFactory.getInstance(filePath).createWritableResource(filePath);

        // initiates file writer
        super.setName(name);
        super.setTransactional(true);
        super.setLineSeparator(lineSeparator);
        super.setEncoding(encoding);
        super.setResource(resource);

        // check auto header
        if(headerCallback == null && withHeader){
            this.setHeaderCallback(createDefaultHeader());
        }

        // open writer
        super.open(executionContext);
    }

    /**
     * createDefaultHeader
     * @return FlatFileHeaderCallback
     */
    protected abstract FlatFileHeaderCallback createDefaultHeader();

    /**
     * createLineAggregator
     * @param itemType
     * @return FixedLengthLineAggregator
     */
    protected abstract LineAggregator createLineAggregator(Class<?> itemType);

    /**
     * write
     * @param items items to write
     */
    @Override
    public final void write(List<? extends T> items) throws Exception {
        if (!getOutputState().isInitialized()) {
            throw new WriterNotOpenException("Writer must be open before it can be written to");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Writing to file with " + items.size() + " items.");
        }

        // call internal write
        internalWrite(items);

        // increase write count
        increaseWriteCount(items.size());
    }

    /**
     * internalWrite
     * @param items items
     */
    public void internalWrite(List<? extends T> items) throws Exception {
        for(T item : items){
            write(item);
        }
    }

    /**
     * write
     * @param item item to write
     */
    public final void write(Object item) throws Exception {

        // register lineAggregator if not exist.
        if(!lineAggregatorRegistry.containsKey(item.getClass())){
            lineAggregatorRegistry.put(item.getClass(), createLineAggregator(item.getClass()));
        }

        // actual write
        LineAggregator<Object> lineAggregator = lineAggregatorRegistry.get(item.getClass());
        state.write(lineAggregator.aggregate(item) + lineSeparator);
    }

    /**
     * increaseWriteCount
     * @param writeCount writeCount
     */
    public final void increaseWriteCount(int writeCount) {
        state.setLinesWritten(state.getLinesWritten() + writeCount);
    }

    @Override
    public final void close() {

        // get write count
        long writeCount = state.getLinesWritten();

        // close
        super.close();

        // put resource
        ResourceHandlerFactory.getInstance(filePath).flushWritableResource(resource, filePath);

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [END] {}}", this.getClass().getSimpleName());
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| itemType: {}", itemType);
        log.info("| writeCount: {}", writeCount);
        log.info("{}", StringUtils.repeat("-", 80));
    }

}