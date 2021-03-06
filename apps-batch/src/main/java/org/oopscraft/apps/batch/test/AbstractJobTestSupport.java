package org.oopscraft.apps.batch.test;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchApplication;
import org.oopscraft.apps.batch.BatchContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j

/**
 * AbstractJobTestSupport
 */
public class AbstractJobTestSupport {

    /**
     * returns current base date for test
     * @return
     */
    public final String getCurrentBaseDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * launchJob
     * @param batchContext
     */
    public final void launchJob(BatchContext batchContext) {
        try {
            List<String> arguments = new ArrayList<>();
            arguments.add(batchContext.getJobClass().getName());
            arguments.add(batchContext.getBaseDate());
            for (String name : batchContext.getJobParameterNames()) {
                String value = batchContext.getJobParameter(name);
                arguments.add(String.format("%s=%s", name, value));
            }
            arguments.add(String.format("_junit_test=%s", UUID.randomUUID().toString()));
            BatchApplication.main(arguments.toArray(new String[arguments.size()]));
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
