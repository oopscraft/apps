package org.oopscraft.apps.core.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class TextTable {

    private static final int MAX_COLUMN_SIZE = 1024;

    Object obj;
    Collection<?> collection;

    /**
     * TextTable
     * @param obj
     */
    public TextTable(Object obj) {
        this.obj = obj;
    }

    /**
     * TextTable
     * @param collection
     */
    public TextTable(Collection<?> collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        if(obj != null) {
            return build(obj);
        }else if(collection != null) {
            return build(collection);
        }else {
            return null;
        }
    }

    /**
     * Builds text table string.
     * @param columnNames
     * @param columnValues
     * @return
     */
    private static String build(String[] columnNames, String[] columnValues) {

        StringBuffer buffer = new StringBuffer();

        // Defines max / min length of value.
        int maxKeySize = 0;
        int maxValSize = 0;
        for(int i = 0, iSize = columnNames.length; i < iSize; i ++ ) {
            String columnName = columnNames[i];
            String columnValue = (columnValues[i] == null ? "null" : columnValues[i]);
            columnValue = columnValue.replaceAll("\t", " ");
            if(columnName.length() > maxKeySize) {
                maxKeySize = columnName.length();
            }
            // line separator
            String[] columnValueLines = columnValue.split("\n");
            if(columnValueLines.length > 0) {
                for(int ii = 0, iiSize = columnValueLines.length; ii < iiSize; ii ++ ) {
                    if(columnValueLines[ii].length() > maxValSize) {
                        maxValSize = columnValueLines[ii].length();
                    }
                }
            }else{
                if(columnValue.length() > maxValSize) {
                    maxValSize = columnValue.length();
                }
            }
        }

        // Prints Text Table
        for(int i = 0, iSize = columnNames.length; i < iSize; i ++ ) {
            if(i == 0) {
                buffer.append( String.format("\n+ %1$-" + maxKeySize + "s + %1$-" + (maxValSize + 20) + "s +", " ").replaceAll(" ", "-"));
                buffer.append( String.format("\n| %1$-" + maxKeySize + "s | %2$-" + (maxValSize + 20) + "s |", "KEY", "VALUE" ));
                buffer.append( String.format("\n+ %1$-" + maxKeySize + "s + %1$-" + (maxValSize + 20) + "s +", " ").replaceAll(" ", "-"));
            }
            String columnName = columnNames[i].replaceAll("\n", "");
            String columnValue = (columnValues[i] == null ? "null" : columnValues[i]);
            columnValue = columnValue.replaceAll("\t", " ");
            // line separator
            String[] columnValueLines = columnValue.split("\n");
            if(columnValueLines.length > 0) {
                for(int ii = 0, iiSize = columnValueLines.length; ii < iiSize; ii ++ ) {
                    buffer.append( String.format("\n| %1$-" + maxKeySize + "s | %2$-" + (maxValSize + 20) + "s |", (ii == 0 ? columnName : ""), columnValueLines[ii]));
                }
            }else{
                buffer.append( String.format("\n| %1$-" + maxKeySize + "s | %2$-" + (maxValSize + 20) + "s |", columnName, columnValue));
            }
            buffer.append( String.format("\n+ %1$-" + maxKeySize + "s + %1$-" + (maxValSize + 20) + "s +", " ").replaceAll(" ", "-"));
        }

        // Returns
        return buffer.toString();
    }

    /**
     * build
     * @param headerNames
     * @param cellValues
     * @return
     */
    private static String build(String[] headerNames, String[][] cellValues) {

        // DEFINES WIDTH
        int[] maxColumnLen = new int[headerNames.length];
        for(int i = 0, iSize = headerNames.length; i < iSize; i ++ ) {
            if(headerNames[i].length() > maxColumnLen[i])
                maxColumnLen[i] = headerNames[i].length();
        }
        for(int i = 0, iSize = cellValues.length; i < iSize; i ++ ) {
            for(int j = 0, iiSize = cellValues[i].length; j < iiSize; j ++ ){
                if((cellValues[i][j] == null ? 0 : cellValues[i][j].length()) > maxColumnLen[j])
                    maxColumnLen[j] = cellValues[i][j].length();
            }
        }

        // CREATE TEXT TABLE
        StringBuffer textTable = new StringBuffer();
        for(int i = 0, iSize = cellValues.length; i < iSize; i ++ ){

            // HEADER
            if(i == 0) {
                StringBuffer head01 = new StringBuffer();
                StringBuffer head02 = new StringBuffer();
                StringBuffer head03 = new StringBuffer();
                for(int j = 0, jSize = headerNames.length; j < jSize; j ++ ) {
                    String columnName = headerNames[j];
                    head01.append( String.format(" %1$-" + maxColumnLen[j] + "s +", " ").replaceAll(" ", "-") );
                    head02.append( String.format(" %1$-" + maxColumnLen[j] + "s |", columnName) );
                    head03.append( String.format(" %1$-" + maxColumnLen[j] + "s +", " ").replaceAll(" ", "-") );
                }
                textTable.append("\n+").append(head01.toString());
                textTable.append("\n|").append(head02.toString());
                textTable.append("\n+").append(head03.toString());
            }

            // RECORD
            StringBuffer cell01 = new StringBuffer();
            StringBuffer cell02 = new StringBuffer();
            for(int j = 0, iiSize = cellValues[i].length; j < iiSize; j ++ ){
                cell01.append( String.format(" %1$-" + maxColumnLen[j] + "s |", cellValues[i][j]) );
                cell02.append( String.format(" %1$-" + maxColumnLen[j] + "s +", " ").replaceAll(" ", "-") );
            }

            textTable.append("\n|").append(cell01.toString());
            textTable.append("\n+").append(cell02.toString());
        }

        return textTable.toString();
    }

    /**
     * parseColumnNames
     * @param obj
     * @return
     */
    private static String[] parseColumnNames(Object obj) {
        String[] columnNames = null;
        if(obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = (Map<String,Object>)obj;
            columnNames = map.keySet().toArray(new String[map.keySet().size()]);
        }else {
            Class<?> objClass = obj.getClass();
            Vector<String> columnNamesVector = new Vector<String>();
            Field[] fields = getFields(objClass);
            for(Field field : fields) {
                String fieldName = field.getName();
                columnNamesVector.add(fieldName);
            }
            columnNames = columnNamesVector.toArray(new String[columnNamesVector.size()]);
        }
        return columnNames;
    }

    /**
     * parseColumnValues
     * @param obj
     * @param columnNames
     * @return
     */
    private static String[] parseColumnValues(Object obj, String[] columnNames) {
        String[] columnValues = new String[columnNames.length];
        if(obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = (Map<String,Object>)obj;
            columnValues = new String[columnNames.length];
            for(int i = 0, iSize = columnNames.length; i < iSize; i ++ ) {
                columnValues[i] = toPrintableValue(map.get(columnNames[i]));
            }
        }else {
            columnValues = new String[columnNames.length];
            for(int i = 0, iSize = columnNames.length; i < iSize; i ++ ) {
                String columnName = columnNames[i];
                Object columnValue = getFieldValue(obj, columnName);
                columnValues[i] = toPrintableValue(columnValue);
            }
        }
        return columnValues;
    }

    private static String toPrintableValue(Object obj) {
        if(obj == null) {
            return null;
        }
        String printableValue = stripWhitespace(obj.toString());
        printableValue = toEllipsis(printableValue, MAX_COLUMN_SIZE - 3);
        return printableValue;
    }

    /**
     * Remove line break and white space chars.
     * @param value
     * @return
     */
    private static String stripWhitespace(String value) {
        if(value == null || value.length() <= 2) {
            return value;
        }
        StringBuffer b = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                if (i > 0 && !Character.isWhitespace(value.charAt(i - 1))) {
                    b.append(' ');
                }
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    /**
     * Gets all field names recursively in super class.
     * @param type
     * @return
     */
    public static Field[] getFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields.toArray(new Field[fields.size()-1]);
    }

    /**
     * Gets field value recursively in super class.
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Object fieldValue = null;
        Class<?> currentClass = obj.getClass();
        do {
           try {
               try {
                   Field field = currentClass.getDeclaredField(fieldName);
                   fieldValue = field.get(obj);
               }catch(Exception e) {
                   Method getterMethod = currentClass.getDeclaredMethod("get" + toPascalCase(fieldName));
                   fieldValue = getterMethod.invoke(obj);
               }
               break;
           } catch(Exception e) {
               fieldValue = e.getMessage();
           }
        } while((currentClass = currentClass.getSuperclass()) != null);
        return fieldValue;
    }

    /**
     * build
     * @param obj
     * @return
     */
    private static String build(Object obj) {
        StringBuffer buffer = new StringBuffer();

        // Checks empty
        if(obj == null) {
            buffer.append("\n-- EMPTY DATA --");
            return buffer.toString();
        }

        String[] columnNames = parseColumnNames(obj);
        String[] columnValues = parseColumnValues(obj,columnNames);
        buffer.append(build(columnNames, columnValues));
        return buffer.toString();
    }


    private static String build(Collection<?> collection) {
        StringBuffer buffer = new StringBuffer();

        // Appends data
        if(collection == null || collection.size() < 1) {
            buffer.append("\n-- EMPTY DATA --");
            return buffer.toString();
        }
        String[] columnNames = parseColumnNames(collection.iterator().next());
        String[][] columnValuesList = new String[collection.size()][];
        Iterator<?> iter = collection.iterator();
        int idx = -1;
        while(iter.hasNext()) {
            idx ++;
            columnValuesList[idx] = parseColumnValues(iter.next(), columnNames);
        }

        buffer.append(build(columnNames, columnValuesList));
        return buffer.toString();
    }

    /**
     * Checks if delimiter char
     * @param c
     * @return
     */
    private static boolean isDelimeterChar(char c) {
        if(c == ' '	|| c == '-' || c == '_'){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Convert string into camel case notation.
     * @param name
     * @return
     */
    public static String toCamelCase(String name) {

        char[] chars = name.toCharArray();
        StringBuffer convertedName = new StringBuffer();
        int convertedNameLen = 0;
        for(int idx = 0; idx < chars.length; idx ++ ) {
            boolean isCamelCase = false;
            char previousChar = idx == 0 ? '\0' : chars[idx-1];
            char currentChar = chars[idx];

            // Checks camel case.
            if(isDelimeterChar(previousChar)) {
                isCamelCase = true;
            }

            // Checks skip chars.
            if(isDelimeterChar(currentChar)) {
                continue;
            }

            // Checks already CamelCase.
            if(Character.isLowerCase(previousChar)
                    && Character.isUpperCase(currentChar)){
                isCamelCase = true;
            }

            // Appends char into camelCase StringBuffer.
            if(convertedNameLen != 0 && isCamelCase) {
                convertedName.append(String.valueOf(currentChar).toUpperCase());
            }else{
                convertedName.append(String.valueOf(currentChar).toLowerCase());
            }

            // Increases count.
            convertedNameLen ++;
        }

        return convertedName.toString();
    }

    /**
     * Convert string into Pascal Case notation.
     * @param name
     * @return
     */
    public static String toPascalCase(String name) {

        char[] chars = name.toCharArray();
        StringBuffer convertedName = new StringBuffer();
        for(int idx = 0; idx < chars.length; idx ++ ) {
            boolean isPascalCase = false;
            char previousChar = idx == 0 ? '\0' : chars[idx-1];
            char currentChar = chars[idx];

            // Checks camel case.
            if(isDelimeterChar(previousChar)) {
                isPascalCase = true;
            }

            // Checks skip chars.
            if(isDelimeterChar(currentChar)) {
                continue;
            }

            // Checks already CamelCase.
            if(Character.isLowerCase(previousChar) && Character.isUpperCase(currentChar)){
                isPascalCase = true;
            }

            if(idx == 0) {
                isPascalCase = true;
            }

            // Appends char into camelCase StringBuffer.
            if(isPascalCase) {
                convertedName.append(String.valueOf(currentChar).toUpperCase());
            }else{
                convertedName.append(String.valueOf(currentChar).toLowerCase());
            }
        }

        return convertedName.toString();
    }

    /**
     * Makes string to ellipsis
     * @param value
     * @param size
     * @return
     */
    public static String toEllipsis(String value, int size) {
        if(value == null) {
            return null;
        }
        if(value.length() > size) {
            return value.substring(0, value.length()-3) + "...";
        }else {
            return value;
        }
    }

}
