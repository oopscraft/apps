package org.oopscraft.apps.batch.item.file;

/**
 * DelimiterFileItemReaderConfigurable
 */
public abstract class DelimiterFileItemReaderConfigurable<Object> extends DelimiterFileItemReader<Object> {

    /**
     * internalRead
     * @param line line
     * @param lineNumber lineNumber
     * @return Object
     */
    public abstract Object internalRead(String line, int lineNumber);

}
