package org.oopscraft.apps.batch.item.file;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.file.transform.FieldConversionService;
import org.oopscraft.apps.batch.item.file.transform.FixedLengthLineTokenizer;
import org.oopscraft.apps.batch.item.file.transform.ItemTypeParser;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;

import java.util.Optional;

@Slf4j
public class FixedLengthFileItemReader<T> extends GenericFileItemReader<T> {

    /**
     * createLineMapper
     * @param itemType
     * @return
     * @throws ItemStreamException
     */
    @Override
    public LineMapper createLineMapper(Class<?> itemType) throws ItemStreamException {
        // item type parser
        ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);

        // line tokenizer
        FixedLengthLineTokenizer lineTokenizer = new FixedLengthLineTokenizer(itemTypeParser);
        lineTokenizer.setCharset(encoding);

        // creates field set mapper
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(itemType);
        FieldConversionService conversionService = new FieldConversionService();
        conversionService.setDateFormat(dateFormat);
        conversionService.setDateTimeFormat(dateTimeFormat);
        conversionService.setTimestampFormat(timestampFormat);
        fieldSetMapper.setConversionService(conversionService);
        fieldSetMapper.setStrict(false);
        fieldSetMapper.setDistanceLimit(1);

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
    public static class FixedLengthFileItemReaderBuilder<T> {

        private String name;

        private String filePath;

        private Class<T> itemType;

        private Boolean withHeader;

        private String encoding;

        private String lineSeparator;

        private String dateFormat;

        private String dateTimeFormat;

        private String timestampFormat;

        /**
         * builder
         * @return
         */
        public FixedLengthFileItemReader<T> build() {
            FixedLengthFileItemReader instance = new FixedLengthFileItemReader();
            Optional.ofNullable(name).ifPresent(value -> instance.setName(value));
            Optional.ofNullable(filePath).ifPresent(value -> instance.setFilePath(value));
            Optional.ofNullable(itemType).ifPresent(value -> instance.setItemType(value));
            Optional.ofNullable(withHeader).ifPresent(value -> instance.setWithHeader(value));
            Optional.ofNullable(encoding).ifPresent(value -> instance.setEncoding(value));
            Optional.ofNullable(lineSeparator).ifPresent(value -> instance.setLineSeparator(value));
            Optional.ofNullable(dateFormat).ifPresent(value -> instance.setDateFormat(value));
            Optional.ofNullable(dateTimeFormat).ifPresent(value -> instance.setDateTimeFormat(value));
            Optional.ofNullable(timestampFormat).ifPresent(value -> instance.setTimestampFormat(value));
            return instance;
        }
    }

    /**
     * builder
     * @return
     */
    public static <T> FixedLengthFileItemReaderBuilder<T> builder() {
        return new FixedLengthFileItemReaderBuilder<T>();
    }

}
