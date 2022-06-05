package org.oopscraft.apps.batch.item.file;

/**
 * FixedLengthFileItemReaderConfigurable
 */
public abstract class FixedLengthFileItemReaderConfigurable<Object> extends FixedLengthFileItemReader<Object> {

    /**
     * internalRead
     * @param line line
     * @param lineNumber lineNumber
     * @return Object
     */
    public abstract Object internalRead(String line, int lineNumber);

}
