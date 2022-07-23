package org.oopscraft.apps.batch.item.file.transform.converter;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
public class DateDeserializer implements GenericConverter {

    @Setter
    protected String dateFormat = BatchConfig.getDateFormat();

    @Setter
    protected String dateTimeFormat = BatchConfig.getDateTimeFormat();

    @Setter
    protected String timestampFormat = BatchConfig.getTimestampFormat();

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair[] pairs = new ConvertiblePair[] {
                new ConvertiblePair(String.class, LocalDateTime.class),
                new ConvertiblePair(String.class, LocalDate.class),
                new ConvertiblePair(String.class, java.util.Date.class),
                new ConvertiblePair(String.class, java.sql.Date.class),
                new ConvertiblePair(String.class, java.sql.Timestamp.class)
        };
        return ImmutableSet.copyOf(pairs);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if(source == null || StringUtils.isEmpty(source.toString())) {
            return null;
        }

        Class<?> targetTypeClass = targetType.getType();
        try {

            // java.time.LocalDateTime
            if (targetTypeClass == LocalDateTime.class) {
                return LocalDateTime.parse(source.toString(), DateTimeFormatter.ofPattern(dateTimeFormat))
                        .withNano(0);
            }

            // java.time.LocalDate
            if (targetTypeClass == LocalDate.class) {
                return LocalDate.parse(source.toString(), DateTimeFormatter.ofPattern(dateFormat));
            }

            // java.util.Date||java.sql.Date
            if (targetTypeClass == java.util.Date.class) {
                return new SimpleDateFormat(dateTimeFormat).parse(source.toString());
            }

            // java.sql.Date
            if(targetTypeClass == java.sql.Date.class){
                return new java.sql.Date(new SimpleDateFormat(dateTimeFormat).parse(source.toString()).getTime());
            }

            // java.sql.Timestamp
            if(targetTypeClass == java.sql.Timestamp.class){
                return new java.sql.Timestamp(new SimpleDateFormat(timestampFormat).parse(source.toString()).getTime());
            }

        }catch(Exception e){
            log.warn("Invalid value[{}]({}):{}", source, targetTypeClass, e.getMessage());
            return null;
        }
        throw new RuntimeException("incompatible type");
    }

}
