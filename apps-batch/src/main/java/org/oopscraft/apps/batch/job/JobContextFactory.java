package org.oopscraft.apps.batch.job;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JobContextFactory {

    /**
     * getBatchContextFromArguments
     * @param arguments
     * @return
     */
    public static JobContext getJobContextFromArguments(String[] arguments) throws ClassNotFoundException {
        Class<?> jobClass = null;
        String baseDate = null;
        Map<String,String> jobParameterMap = new LinkedHashMap<>();
        for(int index = 0; index < arguments.length; index ++ ) {
            String argument = arguments[index];
            // job class
            if(index == 0){
                jobClass = Class.forName(argument);
                jobParameterMap.put("_jobClass", jobClass.getName());
            }
            // base date
            else if(index == 1){
                baseDate = argument;
                jobParameterMap.put("_baseDate", baseDate);
            }
            // job parameter
            else{
                List<String> keyPair = Arrays.asList(argument.split("="));
                String name = keyPair.get(0);
                String value = (keyPair.size() < 2 ? null : keyPair.get(1));
                jobParameterMap.put(name, value);
            }
        }

        // build
        JobContext jobContext = new JobContext();
        jobContext.setJobClass(jobClass);
        jobContext.setBaseDate(baseDate);
        jobContext.setJobParameters(jobParameterMap);
        return jobContext;
    }

}
