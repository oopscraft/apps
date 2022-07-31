package org.oopscraft.apps.core.data;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Map;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource implements DisposableBean {

    public static final String DEFAULT_KEY = "default";

    private static ThreadLocal<String> currentKey = new ThreadLocal<>();

    /**
     * constructor
     * @param defaultKey
     * @param targetDataSources
     */
    public RoutingDataSource(String defaultKey, Map<Object, Object> targetDataSources) {
        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(targetDataSources.get(defaultKey));
    }

    /**
     * sets current key
     * 
     * @param key
     */
    public static void setKey(String key) {
        currentKey.set(key);
    }

    /**
     * getKey
     * @return
     */
    public static String getKey(){
        return currentKey.get();
    }

    /**
     * remove current key
     */
    public static void clearKey() {
        currentKey.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return currentKey.get();
    }

    /**
     * closes element dataSource
     */
    public void close() {
        log.info("RoutingDataSource.close()");
        for (DataSource dataSource : getResolvedDataSources().values()) {
            try {
                log.info("RoutingDataSource.dataSource[{}] close.", dataSource);
                ((HikariDataSource) dataSource).close();
            } catch (Exception ignore) {
                log.warn(ignore.getMessage());
            }
        }
    }

    @PreDestroy
    @Override
    public void destroy() throws Exception {
        this.close();
    }
}
