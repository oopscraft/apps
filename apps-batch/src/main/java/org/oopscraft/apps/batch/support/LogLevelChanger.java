package org.oopscraft.apps.batch.support;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * LogLevelChanger
 */
@Slf4j
public class LogLevelChanger implements Closeable {

    ch.qos.logback.classic.Logger rootLogger;

    ch.qos.logback.classic.Level rootLevel;

    /**
     * LogLevelChanger
     * @param level
     */
    public LogLevelChanger() {
        Object rootLoggerObject = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if(rootLoggerObject != null && rootLoggerObject instanceof Logger) {
            rootLogger = (Logger)rootLoggerObject;
            rootLevel = rootLogger.getLevel();
        }
    }

    /**
     * setLevel
     * @param level
     */
    public void change(Level level) {
        rootLogger.setLevel(ch.qos.logback.classic.Level.valueOf(level.toString()));
    }

    /**
     * close
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            if (rootLogger != null) {
                rootLogger.setLevel(rootLevel);
            }
        }catch(Exception ignore) {
            log.warn(ignore.getMessage());
        }
    }

}