package org.oopscraft.apps.batch.support.tasklet;

import com.github.davidmoten.bigsorter.Serializer;
import com.github.davidmoten.bigsorter.Sorter;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Slf4j
@Builder
public class FileSortTasklet extends AbstractTasklet {

    private String filePathIn;

    private String filePathOut;

    @Singular("sortKey")
    private List<SortKey> sortKeys;

    public enum Type {VARCHAR, NUMERIC }

    public enum Order { ASC, DESC }

    @Builder.Default
    private boolean withHeader = BatchConfig.isWithHeader();

    @Builder.Default
    private String encoding = BatchConfig.getEncoding();

    @Builder.Default
    private String lineSeparator = BatchConfig.getLineSeparator();

    /**
     * SortKey
     */
    @Data
    public static class SortKey {
        private String field;
        private Type type = Type.VARCHAR;
        private Order order = Order.ASC;

        /**
         * of
         * @param field
         * @return
         */
        public static SortKey of(String field) {
            SortKey sortKey = new SortKey();
            sortKey.field = field;
            return sortKey;
        }

        /**
         * of
         * @param field
         * @param type
         * @return
         */
        public static SortKey of(String field, Type type){
            SortKey sortKey = of(field);
            sortKey.type = type;
            return sortKey;
        }

        /**
         * of
         * @param field
         * @param type
         * @param order
         * @return
         */
        public static SortKey of(String field, Type type, Order order){
            SortKey sortKey = of(field, type);
            sortKey.order = order;
            return sortKey;
        }
    }


    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // check parameter validation
        Assert.notNull(filePathIn, "filePathIns must not be null.");
        Assert.notNull(filePathOut, "filePathOut must not be null.");
        Assert.isTrue(sortKeys.size() > 0, "sortKeys must be defined.");

        // defines serializer
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter('\t')
                .withRecordSeparator(lineSeparator);
        Charset charset = Charset.forName(encoding);
        Serializer<CSVRecord> serializer = Serializer.csv(csvFormat, charset);

        // comparator
        Comparator<CSVRecord> comparator = null;
        for(SortKey sortKey : sortKeys){
            if(comparator == null) {
                comparator = createComparator(sortKey);
            }else{
                comparator.thenComparing(createComparator(sortKey));
            }
        }

        // sort
        Sorter.serializer(serializer)
                .comparator(comparator)
                .input(new File(filePathIn))
                .output(new File(filePathOut))
                .sort();
    }

    /**
     * createComparator
     * @param sortKey
     * @return
     */
    private Comparator<CSVRecord> createComparator(SortKey sortKey){
        Comparator<CSVRecord> comparator = (o1, o2) -> {

            // define value
            String field = sortKey.field;
            String value1 = o1.get(field);
            String value2 = o2.get(field);

            // compare to
            int result;
            if(sortKey.type == Type.NUMERIC) {
                result = new BigDecimal(Optional.ofNullable(value1).orElse("0"))
                        .compareTo(new BigDecimal(Optional.ofNullable(value2).orElse("0")));
            }else{
                result = Optional.ofNullable(value1).orElse("")
                        .compareTo(Optional.ofNullable(value2).orElse(""));
            }

            // reverse
            result = result * (sortKey.order == Order.DESC ? -1 : 1);

            // return
            return result;
        };
        return comparator;
    }

}