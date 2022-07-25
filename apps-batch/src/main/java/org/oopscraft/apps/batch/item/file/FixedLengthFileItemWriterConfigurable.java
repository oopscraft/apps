package org.oopscraft.apps.batch.item.file;

import java.util.List;

/**
 * FixedLengthFileItemWriterConfigurable
 */
public abstract class FixedLengthFileItemWriterConfigurable<T> extends FixedLengthFileItemWriter<T> {

    /**
     * constructor
     */
    public FixedLengthFileItemWriterConfigurable() {

    }

    /**
     * internalWrite
     * @param items items
     */
    public abstract void internalWrite(List<? extends T> items) throws Exception;

}
