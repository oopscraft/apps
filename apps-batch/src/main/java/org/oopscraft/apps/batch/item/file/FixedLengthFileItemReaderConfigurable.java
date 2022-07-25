package org.oopscraft.apps.batch.item.file;

/**
 * FixedLengthFileItemReaderConfigurable
 */
public abstract class FixedLengthFileItemReaderConfigurable<T> extends FixedLengthFileItemReader<T> {

    /**
     * constructor
     */
    public FixedLengthFileItemReaderConfigurable() {
        super.setWithHeader(false);
    }

    /**
     * internalRead
     * @param line line
     * @param lineNumber lineNumber
     * @return Object
     */
    public abstract T internalRead(String line, int lineNumber);

}
