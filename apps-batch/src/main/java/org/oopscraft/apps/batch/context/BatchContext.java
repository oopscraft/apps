package org.oopscraft.apps.batch.context;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.text.SimpleDateFormat;
import java.util.*;

@Data
@NoArgsConstructor
public class BatchContext {

    private Class<?> jobClass;

    private String baseDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

    private Map<String,String> jobParameters = new LinkedHashMap<>();

    /**
     * setJobParameter
     * @param name
     * @param value
     */
    public void setJobParameter(String name, String value){
        jobParameters.put(name, value);
    }

    /**
     * getJobParameter
     * @param name
     * @return
     */
    public String getJobParameter(String name){
        return jobParameters.get(name);
    }

    /**
     * getJobParameters
     * @return
     */
    public JobParameters getJobParameters() {
        Map<String, JobParameter> jobParameterMap = new LinkedHashMap<>();
        for(String name : jobParameters.keySet()){
            String value = jobParameters.get(name);
            jobParameterMap.put(name, new JobParameter(value));
        }
        return new JobParameters(jobParameterMap);
    }

    /**
     * getJobParameterNames
     * @return
     */
    public List<String> getJobParameterNames() {
        List<String> jobParameterNames = new ArrayList<>();
        for(String name : jobParameters.keySet()){
            jobParameterNames.add(name);
        }
        return jobParameterNames;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class BatchContextBuilder {

        protected Class<?> jobClass;

        protected String baseDate;

        protected Map<String,String> jobParameters = new LinkedHashMap<>();

        public BatchContextBuilder jobParameter(String key, String value) {
            jobParameters.put(key, value);
            return this;
        }

        /**
         * build
         * @return
         */
        public BatchContext build() {
            BatchContext instance = new BatchContext();
            Optional.ofNullable(jobClass).ifPresent(value -> {
                instance.setJobClass(value);
            });
            Optional.ofNullable(baseDate).ifPresent(value -> {
                instance.setBaseDate(value);
            });
            instance.setJobParameters(jobParameters);
            return instance;
        }
    }

    /**
     * builder
     * @return
     */
    public static BatchContextBuilder builder() {
        return new BatchContextBuilder();
    }

}
