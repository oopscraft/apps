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
        public DelimiterFileItemWriter<T> build() {
            DelimiterFileItemWriter instance = new DelimiterFileItemWriter();
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
    public static <T> DelimiterFileItemWriterBuilder<T> builder() {
        return new DelimiterFileItemWriterBuilder<T>();
    }

}