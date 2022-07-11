package org.oopscraft.apps.batch.item.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.file.transform.DelimitedLineAggregator;
import org.oopscraft.apps.batch.item.file.transform.FieldConversionService;
import org.oopscraft.apps.batch.item.file.transform.ItemField;
import org.oopscraft.apps.batch.item.file.transform.ItemTypeParser;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DelimiterFileItemWriter<T> extends GenericFileItemWriter<T> {

    @Setter
    @Getter
    private String delimiter = String.valueOf('\t');

    /**
     * createDefaultHeader
     * @return
     */
    @Override
    protected FlatFileHeaderCallback createDefaultHeader() {
        return writer -> {
            List<String> fieldNames = new ArrayList<>();
            ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);
            itemTypeParser.getItemFields().forEach(itemField-> fieldNames.add(itemField.getName()));
            writer.write(String.join(delimiter, fieldNames));
        };
    }

    /**
     * createLineAggregator
     * @param itemType
     * @return
     */
    @Override
    protected LineAggregator createLineAggregator(Class<?> itemType) {
        // item type parser
        ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);

        // field extractor
        BeanWrapperFieldExtractor<Object> fieldExtractor = new BeanWrapperFieldExtractor<>();
        String[] names = itemTypeParser.getItemFields().stream()
                .map(ItemField::getName)
                .toArray(String[]::new);
        fieldExtractor.setNames(names);

        // line aggregator
        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator(itemTypeParser);
        lineAggregator.setCharset(encoding);
        lineAggregator.setDelimiter(delimiter);
        lineAggregator.setFieldExtractor(fieldExtractor);
        FieldConversionService conversionService = new FieldConversionService();
        conversionService.setDateFormat(dateFormat);
        conversionService.setDateTimeFormat(dateTimeFormat);
        conversionService.setTimestampFormat(timestampFormat);
        lineAggregator.setConversionService(conversionService);
        return lineAggregator;
    }


    /**
     * FixedLengthFileItemWriterBuilder
     * @param
     */
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class DelimiterFileItemWriterBuilder<T> {

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
        public DelimiterFileItemWriter<T> build() {
            DelimiterFileItemWriter instance = new DelimiterFileItemWriter();
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
    public static <T> DelimiterFileItemWriterBuilder<T> builder() {
        return new DelimiterFileItemWriterBuilder<T>();
    }

}