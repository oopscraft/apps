package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariConfig;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "batch")
@ConstructorBinding
@RequiredArgsConstructor
public class BatchConfig {

    @Getter
    private static String dataHome = "dat/";

    @Getter
    private static Map<String,String> dataHomeProperties = new LinkedHashMap<>();

    @Getter
    private static Class<?> dataDirectoryStrategy = DataDirectoryStrategy.class;

    @Getter
    private static boolean withHeader = true;

    @Getter
    private static String encoding = "UTF-8";

    @Getter
    private static String lineSeparator = "\n";

    @Getter
    private static String dateFormat = "yyyy-MM-dd";

    @Getter
    private static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Getter
    private static String timestampFormat = "yyyy-MM-dd HH:mm:ss";

    @Getter
    private final boolean enableDatasource;

    @Getter
    private final HikariConfig datasource;


    /**
     * setDataHome
     * @param value
     */
    public void setDataHome(String value) {
        dataHome = value;
    }

    /**
     * setDataHomeProperties
     * @param dataHomeProperties
     */
    public void setDataHomeProperties(Map<String,String> dataHomeProperties){
        this.dataHomeProperties = dataHomeProperties;
    }

    /**
     * DataDirectoryStrategy
     */
    public static class DataDirectoryStrategy {
        public String get(Class<?> clazz){
            return clazz.getName().replaceAll("\\.","/").concat("/");
        }
    }

    /**
     * setDataDirectoryStrategy
     * @param value
     * @throws ClassNotFoundException
     */
    public void setDataDirectoryStrategy(String value) throws ClassNotFoundException {
        dataDirectoryStrategy = Class.forName(value);
    }

    /**
     * getDataDirectory
     * @param object
     * @return
     */
    public static String getDataDirectory(Object object) {
        return getDataDirectory(object.getClass());
    }

    /**
     * getDataDirectory
     * @param clazz
     * @return
     */
    public static String getDataDirectory(Class<?> clazz) {
        try {
            DataDirectoryStrategy instance = (DataDirectoryStrategy) dataDirectoryStrategy.getConstructor().newInstance();
            return dataHome + instance.get(clazz);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
