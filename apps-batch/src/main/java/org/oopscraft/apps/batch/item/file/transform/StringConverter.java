package org.oopscraft.apps.batch.item.file.transform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

@Slf4j
@Deprecated
public class StringConverter {

    /**
     * truncate Malformed
     *
     * @param value
     * @param maxLength
     * @param charset
     * @return
     */
    public static String truncateMalformed(String value, Charset charset, int maxLength) {
        byte[] valueBytes = value.getBytes(charset);
        int valueLength = Math.min(maxLength, valueBytes.length);
        CharsetDecoder charsetDecoder = charset.newDecoder();
        // Ensure truncating by having byte buffer = DB_FIELD_LENGTH
        ByteBuffer byteBuffer = ByteBuffer.wrap(valueBytes, 0, valueLength); // len in [B]
        CharBuffer charBuffer = CharBuffer.allocate(valueLength); // len in [char] <= # [B]
        // Ignore an incomplete character
        charsetDecoder.onMalformedInput(CodingErrorAction.IGNORE);
        charsetDecoder.decode(byteBuffer, charBuffer, true);
        charsetDecoder.flush(charBuffer);
        value = new String(charBuffer.array(), 0, charBuffer.position());
        return value;
    }

    /**
     * isCharset
     * @param value
     * @param charset
     * @return
     */
    public static boolean isCharset(String value, Charset charset) {
        String checked = new String(value.getBytes(charset), charset);
        return !checked.equals(value);
    }

    /**
     * testCharset
     * @param value
     */
    public static void testCharset(String value) {
        String[] charSet = {"UTF-8", "EUC-KR", "KSC5601", "ISO-8859-1", "X-WINDOWS-949"};
        for (int i = 0; i < charSet.length; i++) {
            for (int j = 0; j < charSet.length; j++) {
                try {
                    log.debug("[{},{}] = {}", charSet[i], charSet[j], new String(value.getBytes(charSet[i]), charSet[j]));
                } catch (UnsupportedEncodingException e) {
                    log.warn("{}", e.getMessage());
                }
            }
        }
    }


    /**
     * convertLineWithDelimiter
     * @param values
     * @param delimiter
     * @param charset
     * @return
     */
    @Deprecated
    public static String convertLineWithDelimiter(String[] values, String delimiter, Charset charset) {
        Assert.notNull(values, "values must not be null.");
        // changes encoding
        for(int i = 0, size = values.length; i < size; i ++ ){
            String value = values[i];
            int length = value.getBytes(charset).length;
            value = truncateMalformed(value, charset, length);
            values[i] = value;
        }

        // convert to line
        return StringUtils.arrayToDelimitedString(values, delimiter);

    }

    /**
     * convertLineWithLength
     * @param values
     * @param lengths
     * @param charset
     * @return
     */
    @Deprecated
    public static String convertLineWithLength(String[] values, int[] lengths, Charset charset) {
        Assert.notNull(values, "values must not be null.");
        Assert.notNull(lengths, "values must not be null.");
        Assert.isTrue(values.length == lengths.length, "values and lengths must be same size.");
        ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                int length = lengths[i];
                value = truncateMalformed(value, charset, length);
                byte[] valueBytes = value.getBytes(charset);
                byte[] targetBytes = new byte[length];
                Arrays.fill(targetBytes, (byte) ' ');
                System.arraycopy(valueBytes, 0, targetBytes, 0, Math.min(valueBytes.length, length));
                lineBytes.write(targetBytes);
            }
            String line = new String(lineBytes.toByteArray(), charset);
            return line;
        }catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            if (lineBytes != null) {
                try {
                    lineBytes.close();
                } catch (Exception ignore) {}
            }
        }
    }



}
