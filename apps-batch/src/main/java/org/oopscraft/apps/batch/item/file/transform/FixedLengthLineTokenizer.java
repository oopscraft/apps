package org.oopscraft.apps.batch.item.file.transform;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.batch.item.file.transform.Range;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FixedLengthLineTokenizer extends AbstractLineTokenizer {

    @Setter
    private String charset = BatchConfig.getEncoding();

    private final List<Range> ranges = new ArrayList<>();

    /**
     * FixedLengthLineTokenizer
     * @param itemTypeParser itemTypeParser
     */
    public FixedLengthLineTokenizer(ItemTypeParser itemTypeParser) {
        super();

        // set names
        String[] names = itemTypeParser.getItemFields().stream()
                .map(ItemField::getName)
                .toArray(String[]::new);
        setNames(names);

        // defines ranges
        int position = 0;
        for(ItemField itemField : itemTypeParser.getItemFields()){
            int fieldLength = itemField.getLength().value();
            int start = position + 1;
            int end = start + fieldLength -1;
            ranges.add(new Range(start, end));
            position = end;
        }
    }

    /**
     * doTokenize
     * @param line line
     * @return tokenized strings
     */
    @Override
    protected List<String> doTokenize(String line) {
        try {
            List<String> tokens = new ArrayList<>();
            byte[] byteString = line.getBytes(charset);
            int lineLength = byteString.length;
            for (Range range : ranges) {
                String token;
                int startPos = range.getMin() -1;
                int endPos = range.getMax();
                if (lineLength >= endPos) {
                    token = new String(byteString, startPos, endPos - startPos, charset);
                } else if (lineLength >= startPos) {
                    token = new String(byteString, startPos, lineLength - startPos, charset);
                } else {
                    token = "";
                }
                tokens.add(token);
            }
            return tokens;
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
