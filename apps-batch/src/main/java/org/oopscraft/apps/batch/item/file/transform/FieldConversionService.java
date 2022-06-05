package org.oopscraft.apps.batch.item.file.transform;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.transform.converter.*;
import org.springframework.format.support.FormattingConversionService;

/**
 * FieldConversionService
 */
@Slf4j
public class FieldConversionService extends FormattingConversionService {

    @Setter
    protected String dateFormat = BatchConfig.getDateFormat();

    @Setter
    protected String dateTimeFormat = BatchConfig.getDateTimeFormat();

    @Setter
    protected String timestampFormat = BatchConfig.getTimestampFormat();

    /**
     * FieldConversionService
     */
    public FieldConversionService() {
        super();

        // date type
        DateSerializer dateSerializer = new DateSerializer();
        dateSerializer.setDateFormat(dateFormat);
        dateSerializer.setDateTimeFormat(dateTimeFormat);
        dateSerializer.setTimestampFormat(timestampFormat);
        addConverter(dateSerializer);

        DateDeserializer dateDeserializer = new DateDeserializer();
        dateDeserializer.setDateFormat(dateFormat);
        dateDeserializer.setDateTimeFormat(dateTimeFormat);
        dateDeserializer.setTimestampFormat(timestampFormat);
        addConverter(dateDeserializer);

        // numeric type
        this.addConverter(new NumericSerializer());
        this.addConverter(new NumericDeserializer());

        // default last
        this.addConverter(new DefaultSerializer());
        this.addConverter(new DefaultDeserializer());
    }


}
