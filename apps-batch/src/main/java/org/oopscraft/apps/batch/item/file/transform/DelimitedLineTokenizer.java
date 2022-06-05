package org.oopscraft.apps.batch.item.file.transform;

import lombok.Setter;
import org.oopscraft.apps.batch.BatchConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * DelimitedLineTokenizer
 */
public class DelimitedLineTokenizer extends org.springframework.batch.item.file.transform.DelimitedLineTokenizer {

    @Setter
    private String charset = BatchConfig.getEncoding();

    /**
     * DelimitedLineTokenizer
     * @param itemTypeParser itemTypeParser
     */
    public DelimitedLineTokenizer(ItemTypeParser itemTypeParser) {
        super();

        // set names
        String[] names = itemTypeParser.getItemFields().stream()
                .map(ItemField::getName)
                .toArray(String[]::new);
        super.setNames(names);
    }

    /**
     * doTokenize
     * @param line line
     * @return tokenized strings
     */
    @Override
    protected List<String> doTokenize(String line) {
        List<String> tokens = super.doTokenize(line);

        // converts charset
        List<String> values = new ArrayList<>();
        for(String token : tokens) {
            values.add(token);
        }

        // returns
        return values;
    }


}
