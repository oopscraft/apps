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

        private String name;

        private String filePath;

        private Class<T> itemType;

        private Boolean withHeader;

        private String encoding;

        private String lineSeparator;

        private String dateFormat;

        private String dateTimeFormat;

        private String timestampFormat;

        private String delimiter;

        /**
         * build
         * @return
         */
        public DelimiterFileItemReader<T> build() {
            DelimiterFileItemReader instance = new DelimiterFileItemReader();
            Optional.ofNullable(name).ifPresent(value -> instance.setName(value));
            Optional.ofNullable(filePath).ifPresent(value -> instance.setFilePath(value));
            Optional.ofNullable(itemType).ifPresent(value -> instance.setItemType(value));
            Optional.ofNullable(withHeader).ifPresent(value -> instance.setWithHeader(value));
            Optional.ofNullable(encoding).ifPresent(value -> instance.setEncoding(value));
            Optional.ofNullable(lineSeparator).ifPresent(value -> instance.setLineSeparator(value));
            Optional.ofNullable(dateFormat).ifPresent(value -> instance.setDateFormat(value));
            Optional.ofNullable(dateTimeFormat).ifPresent(value -> instance.setDateTimeFormat(value));
            Optional.ofNullable(timestampFormat).ifPresent(value -> instance.setTimestampFormat(value));
            Optional.ofNullable(delimiter).ifPresent(value -> instance.setDelimiter(value));
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
