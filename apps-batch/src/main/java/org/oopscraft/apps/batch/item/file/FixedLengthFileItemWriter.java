package org.oopscraft.apps.batch.item.file;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.item.file.transform.FieldConversionService;
import org.oopscraft.apps.batch.item.file.transform.FixedLengthLineAggregator;
import org.oopscraft.apps.batch.item.file.transform.ItemField;
import org.oopscraft.apps.batch.item.file.transform.ItemTypeParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FixedLengthFileItemWriter<T> extends GenericFileItemWriter<T> {

    /**
     * createDefaultHeader
     * @return
     */
    protected FlatFileHeaderCallback createDefaultHeader() {
        return writer -> {
            List<String> fieldNames = new ArrayList<>();
            ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);
            itemTypeParser.getItemFields().forEach(itemField->{
                String fieldName = itemField.getName();
                int fieldLength = itemField.getLength().value();
                fieldName = StringUtils.rightPad(fieldName, fieldLength);
                fieldName = StringUtils.truncate(fieldName, fieldLength);
                fieldNames.add(fieldName);
            });
            writer.write(String.join("",fieldNames));
        };
    }

    /**
     * createFixedLengthLineAggregator
     * @param itemType item type class
     * @return FixedLengthLineAggregator
     */
    public LineAggregator createLineAggregator(Class<?> itemType) {

        // item type parser
        ItemTypeParser itemTypeParser = new ItemTypeParser(itemType);

        // field extractor
        BeanWrapperFieldExtractor<Object> fieldExtractor = new BeanWrapperFieldExtractor<>();
        String[] names = itemTypeParser.getItemFields().stream()
            .map(ItemField::getName)
            .toArray(String[]::new);
        fieldExtractor.setNames(names);

        // line aggregator
        FixedLengthLineAggregator lineAggregator = new FixedLengthLineAggregator(itemTypeParser);
        lineAggregator.setCharset(encoding);
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
    public static class FixedLengthFileItemWriterBuilder<T> {
        protected String name;
        protected String filePath;
        protected Class<T> itemType;
        protected Boolean withHeader;
        protected String encoding;
        protected String lineSeparator;
        protected String dateFormat;
        protected String dateTimeFormat;
        protected String timestampFormat;
        public FixedLengthFileItemWriter<T> build() {
            FixedLengthFileItemWriter instance = new FixedLengthFileItemWriter();
            instance.setName(Optional.ofNullable(name).orElse(instance.name));
            instance.setFilePath(Optional.ofNullable(filePath).orElse(instance.filePath));
            instance.setItemType(Optional.ofNullable(itemType).orElse(instance.itemType));
            instance.setWithHeader(Optional.ofNullable(withHeader).orElse(instance.withHeader));
            instance.setEncoding(Optional.ofNullable(encoding).orElse(instance.encoding));
            instance.setLineSeparator(Optional.ofNullable(lineSeparator).orElse(instance.lineSeparator));
            instance.setDateFormat(Optional.ofNullable(dateFormat).orElse(instance.dateFormat));
            instance.setDateTimeFormat(Optional.ofNullable(dateTimeFormat).orElse(instance.dateTimeFormat));
            instance.setTimestampFormat(Optional.ofNullable(timestampFormat).orElse(instance.timestampFormat));
            return instance;
        }
    }

    /**
     * builder
     * @return
     */
    public static <T> FixedLengthFileItemWriterBuilder<T> builder() {
        return new FixedLengthFileItemWriterBuilder<T>();
    }

}