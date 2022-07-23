package org.oopscraft.apps.batch.item.file.transform.converter;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
public class DateSerializer implements GenericConverter {

    @Setter
    protected String dateFormat = BatchConfig.getDateFormat();

    @Setter
    protected String dateTimeFormat = BatchConfig.getDateTimeFormat();

    @Setter
    protected String timestampFormat = BatchConfig.getTimestampFormat();

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair[] pairs = new ConvertiblePair[] {
                new ConvertiblePair(LocalDateTime.class, String.class),
                new ConvertiblePair(LocalDate.class, String.class),
                new ConvertiblePair(java.util.Date.class, String.class),
                new ConvertiblePair(java.sql.Date.class, String.class),
                new ConvertiblePair(java.sql.Timestamp.class, String.class)
        };
        return ImmutableSet.copyOf(pairs);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if(source == null) {
            return "";
        }

        Class<?> sourceTypeClass = sourceType.getType();
        try {

            // java.time.LocalDateTime
            if (sourceTypeClass == LocalDateTime.class) {
                LocalDateTime localDateTime = (LocalDateTime)source;
                return DateTimeFormatter.ofPattern(dateTimeFormat).format(localDateTime.withNano(0));
            }

            // java.time.LocalDate
            if (sourceTypeClass == LocalDate.class) {
                LocalDate localDate = (LocalDate)source;
                return DateTimeFormatter.ofPattern(dateFormat).format(localDate);
            }

            // java.util.Date|java.sql.Date
            if (sourceTypeClass == java.util.Date.class
            || sourceTypeClass == java.sql.Date.class) {
                return new SimpleDateFormat(dateTimeFormat).format(source);
            }

            // java.sql.Timestamp type
            if(sourceTypeClass == java.sql.Timestamp.class){
                return new SimpleDateFormat(timestampFormat).format(source);
            }

        }catch(Exception e){
            log.warn("Invalid value[{}]({}):{}", source, sourceTypeClass, e.getMessage());
            return "";
        }
        throw new RuntimeException("incompatible type");
    }

}
