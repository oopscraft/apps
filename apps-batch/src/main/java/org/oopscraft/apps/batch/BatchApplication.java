package org.oopscraft.apps.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.context.BatchContextFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

import java.util.stream.Collectors;

@Slf4j
@Import(BatchConfiguration.class)
@RequiredArgsConstructor
public class BatchApplication implements CommandLineRunner {

    private static BatchContext batchContext;

    private final JobLauncher jobLauncher;

    private final Job job;

    /**
     * main
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // parse batch context
        if(args == null || args.length < 1) {
            throw new IllegalArgumentException("Usage: BatchApplication [jobClass] [args]...");
        }
        batchContext = BatchContextFactory.getJobContextFromArguments(args);
        BatchConfiguration.setBatchContext(batchContext);

        // runs spring application
        new SpringApplicationBuilder(BatchApplication.class)
                .sources(batchContext.getJobClass())
                .properties("--spring.batch.job.enabled=false")
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .web(WebApplicationType.NONE)
                .registerShutdownHook(true)
                .lazyInitialization(true)
                .run(args);
    }


    @Override
    public void run(String... args) throws Exception {

        // launch
        JobExecution jobExecution = jobLauncher.run(job, batchContext.getJobParameters());
        if(jobExecution.getStatus().isUnsuccessful()){
            String failureExceptions = jobExecution.getFailureExceptions().stream()
                    .map(e -> e.getMessage())
                    .collect(Collectors.joining());
            log.error("failureExceptions:{}", failureExceptions);
            throw new RuntimeException(jobExecution + failureExceptions);
        }
    }

}
