package org.oopscraft.apps.batch.item.file;

/**
 * DelimiterFileItemReaderConfigurable
 */
public abstract class DelimiterFileItemReaderConfigurable<T> extends DelimiterFileItemReader<T> {

	/**
	 * constructor
	 */
	public DelimiterFileItemReaderConfigurable(){
		this.setWithHeader(false);
	}

    /**
     * internalRead
     * @param line line
     * @param lineNumber lineNumber
     * @return Object
     */
    public abstract T internalRead(String line, int lineNumber);

}
