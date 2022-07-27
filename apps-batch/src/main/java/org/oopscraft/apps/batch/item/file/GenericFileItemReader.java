package org.oopscraft.apps.batch.item.file;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.resource.ResourceHandlerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public abstract class GenericFileItemReader<T> extends FlatFileItemReader<T> {

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

    protected Resource resource;

    protected HashMap<Class<?>, LineMapper> lineMapperRegistry = new LinkedHashMap<>();

    protected int readCount = 0;

    @Override
    protected final void doOpen() throws Exception {

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [OPEN] GenericFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| itemType: {}", itemType);
        log.info("| withHeader: {}", withHeader);
        log.info("| encoding: {}", encoding);
        log.info("{}", StringUtils.repeat("-",80));

        // checks validation
        Assert.notNull(name, "name must be defined");
        Assert.notNull(filePath, "filePath must be defined");
        Assert.notNull(itemType, "itemType must be defined");

        // creates resource
        resource = ResourceHandlerFactory.getInstance(filePath).createReadableResource(filePath);

        // creates file reader
        super.setName(name);
        super.setEncoding(encoding);
        super.setResource(resource);

        // checks auto header
        if(withHeader){
            this.setLinesToSkip(1);
        }

        // open
        super.doOpen();
    }

    /**
     * createLineMapper
     * @param itemType
     * @return
     * @throws ItemStreamException
     */
    public abstract LineMapper createLineMapper(Class<?> itemType) throws ItemStreamException;

    /**
     * doRead
     * @return T
     */
    @Override
    protected final T doRead() throws Exception {
        String line = readLine();
        if (line == null) {
            return null;
        } else {
            readCount ++;
            if(log.isDebugEnabled()) {
                log.debug("READ [{}]", line);
            }
            return internalRead(line, this.getCurrentItemCount());
        }
    }

    /**
     * readLine
     * @return String
     */
    private final String readLine() throws Exception {
        Method readLine = FlatFileItemReader.class.getDeclaredMethod("readLine");
        readLine.setAccessible(true);
        return (String)readLine.invoke(this);
    }

    /**
     * internalRead
     * @param line line
     * @return Object
     */
    public T internalRead(String line, int lineNumber) {
        return mapLine(line, itemType);
    }

    /**
     * mapLine
     * @param line line
     * @param itemType itemType
     * @return Object
     */
    public T mapLine(String line, Class<?> itemType) {
        try {
            if(!lineMapperRegistry.containsKey(itemType)){
                lineMapperRegistry.put(itemType, createLineMapper(itemType));
            }
            LineMapper<T> lineMapper = lineMapperRegistry.get(itemType);
            return lineMapper.mapLine(line, 1);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected final void doClose() throws Exception {

        // close
        super.doClose();

        // logging
        log.info("{}", StringUtils.repeat("-", 80));
        log.info("| [CLOSE] GenericFileItemReader");
        log.info("| name: {}", name);
        log.info("| filePath: {}", filePath);
        log.info("| itemType: {}", itemType);
        log.info("| withHeader: {}", withHeader);
        log.info("| encoding: {}", encoding);
        log.info("| readCount: {}", readCount);
        log.info("{}", StringUtils.repeat("-", 80));
    }



}
