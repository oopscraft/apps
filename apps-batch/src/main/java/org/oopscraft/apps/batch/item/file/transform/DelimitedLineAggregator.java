package org.oopscraft.apps.batch.item.file.transform;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.springframework.batch.item.file.transform.ExtractorLineAggregator;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DelimitedLineAggregator
 * @param
 */
@Slf4j
public class DelimitedLineAggregator extends ExtractorLineAggregator<Object> {

    private final ItemTypeParser itemTypeParser;

    @Setter
    private String charset = BatchConfig.getEncoding();

    @Setter
    private String delimiter;

    @Setter
    private FieldConversionService conversionService;

    /**
     * DelimitedLineAggregator
     * @param itemTypeParser itemTypeParser
     */
    public DelimitedLineAggregator(ItemTypeParser itemTypeParser){
        super();
        this.itemTypeParser = itemTypeParser;
    }

    @Override
    public String doAggregate(Object[] fields) {
        try {
            List<String> values = new ArrayList<>();
            for(Object field : fields) {
                String value = Optional.ofNullable(conversionService.convert(field, String.class)).orElse("");
                values.add(value);
            }

            // convert to line
            return StringUtils.arrayToDelimitedString(values.toArray(new String[0]), delimiter);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
