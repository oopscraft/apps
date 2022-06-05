package org.oopscraft.apps.batch.item.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.file.transform.DelimitedLineTokenizer;
import org.oopscraft.apps.batch.item.file.transform.FieldConversionService;
import org.oopscraft.apps.batch.item.file.transform.ItemTypeParser;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;

import java.util.Optional;

@Slf4j
public class DelimiterFileItemReader<Object> extends GenericFileItemReader<Object> {

    @Setter
    @Getter
    private String delimiter = String.valueOf('\t');

    /**
     * createLineMapper
     * @param itemType
     * @return
     * @throws ItemStreamException
     */
    @Override
    public LineMapper<Object> createLineMapper(Class<? extends Object> itemType) throws ItemStreamException {
        // item type parser
        ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);

        // lineTokenizer
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(itemTypeParser);
        lineTokenizer.setDelimiter(delimiter);
        lineTokenizer.setStrict(false);

        // creates field set mapper
        BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(itemType);
        FieldConversionService conversionService = new FieldConversionService();
        conversionService.setDateFormat(dateFormat);
        conversionService.setDateTimeFormat(dateTimeFormat);
        conversionService.setTimestampFormat(timestampFormat);
        fieldSetMapper.setConversionService(conversionService);

        // return lineMapper
        DefaultLineMapper<Object> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }


    /**
     * FixedLengthFileItemWriterBuilder
     * @param
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class DelimiterFileItemReaderBuilder<T> {
        protected String name;
        protected String filePath;
        protected Class<T> itemType;
        protected Boolean withHeader;
        protected String encoding;
        protected String lineSeparator;
        protected String dateFormat;
        protected String dateTimeFormat;
        protected String timestampFormat;
        protected String delimiter;
        public DelimiterFileItemReader<T> build() {
            DelimiterFileItemReader instance = new DelimiterFileItemReader();
            instance.setName(Optional.ofNullable(name).orElse(instance.name));
            instance.setFilePath(Optional.ofNullable(filePath).orElse(instance.filePath));
            instance.setItemType(Optional.ofNullable(itemType).orElse(instance.itemType));
            instance.setWithHeader(Optional.ofNullable(withHeader).orElse(instance.withHeader));
            instance.setEncoding(Optional.ofNullable(encoding).orElse(instance.encoding));
            instance.setLineSeparator(Optional.ofNullable(lineSeparator).orElse(instance.lineSeparator));
            instance.setDateFormat(Optional.ofNullable(dateFormat).orElse(instance.dateFormat));
            instance.setDateTimeFormat(Optional.ofNullable(dateTimeFormat).orElse(instance.dateTimeFormat));
            instance.setTimestampFormat(Optional.ofNullable(timestampFormat).orElse(instance.timestampFormat));
            instance.setDelimiter(Optional.ofNullable(delimiter).orElse(instance.delimiter));
            return instance;
        }
    }

    /**
     * builder
     * @return
     */
    public static <T> DelimiterFileItemReaderBuilder<T> builder() {
        return new DelimiterFileItemReaderBuilder<T>();
    }

}
