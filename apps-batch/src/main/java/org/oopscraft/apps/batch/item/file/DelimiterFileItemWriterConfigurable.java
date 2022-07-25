package org.oopscraft.apps.batch.item.file;

import java.util.List;

/**
 * DelimiterFileItemWriterConfigurable
 */
public abstract class DelimiterFileItemWriterConfigurable<T> extends DelimiterFileItemWriter<T> {

    /**
     * constructor
     */
    public DelimiterFileItemWriterConfigurable() {
        this.setWithHeader(false);
    }

    /**
     * internalWrite
     * @param items items
     */
    public abstract void internalWrite(List<? extends T> items) throws Exception;

}
