package org.oopscraft.apps.batch.item.file.transform;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.springframework.batch.item.file.transform.ExtractorLineAggregator;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FixedLengthLineAggregator extends ExtractorLineAggregator<Object> {

    private final ItemTypeParser itemTypeParser;

    @Setter
    private String charset = BatchConfig.getEncoding();

    @Setter
    private FieldConversionService conversionService;

    /**
     * FixedLengthLineAggregator
     * @param itemTypeParser itemTypeParser
     */
    public FixedLengthLineAggregator(ItemTypeParser itemTypeParser){
        super();
        this.itemTypeParser = itemTypeParser;
    }

    /**
     * doAggregate
     * @param fields fields
     * @return String
     */
    @Override
    protected String doAggregate(Object[] fields) {
        try (ByteArrayOutputStream lineBytes = new ByteArrayOutputStream()) {
            List<ItemField> itemFields = itemTypeParser.getItemFields();
            for(int i = 0, size = itemFields.size(); i < size; i++) {

                // defines
                ItemField itemField = itemFields.get(i);
                String value = Optional.ofNullable(conversionService.convert(fields[i], String.class)).orElse("");
                int length = itemField.getLength().value();
                char padChar = (itemField.getLength().padChar() == '\0' ? ' ' : itemField.getLength().padChar());

                // allocates bytes
                byte[] sourceBytes = value.getBytes(charset);
                byte[] targetBytes = new byte[length];
                Arrays.fill(targetBytes, (byte)padChar);

                // array copy
                if(itemField.getLength().align() == Align.RIGHT) {
                    System.arraycopy(sourceBytes, 0, targetBytes,  Math.max(length-sourceBytes.length,0), Math.min(sourceBytes.length, length));
                }else{
                    System.arraycopy(sourceBytes, 0, targetBytes, 0, Math.min(sourceBytes.length, length));
                }

                // write to buffer
                lineBytes.write(targetBytes);
            }

            // returns
            return lineBytes.toString(charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
