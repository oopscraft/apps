package org.oopscraft.apps.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.context.BatchContextFactory;
import org.springframework.batch.core.BatchStatus;
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
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Import(BatchConfiguration.class)
@RequiredArgsConstructor
public class BatchApplication implements CommandLineRunner, ApplicationContextAware {

    private static ConfigurableApplicationContext applicationContext = null;

    private static BufferingApplicationStartup bufferingApplicationStartup = new BufferingApplicationStartup(1000);

    private static BatchContext batchContext = null;

    private final Job job;

    private final JobLauncher jobLauncher;

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

    /**
     * setApplicationContext
     * @param applicationContextInstance applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContextInstance) throws BeansException {
        applicationContext = (ConfigurableApplicationContext) applicationContextInstance;
    }

    /**
     * run
     * @param args program arguments
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // profiles startup process
            printStartupProfile();

            // logging startup context
            printStartupContext(args);

            // launch
            JobExecution jobExecution = jobLauncher.run(job, batchContext.getJobParameters());
            log.info("JobExecution:{}", jobExecution);

            // check isUnsuccessful
            if(jobExecution.getStatus().isUnsuccessful()){
                String failureExceptions = jobExecution.getFailureExceptions().stream()
                        .map(ExceptionUtils::getStackTrace)
                        .collect(Collectors.joining());
                throw new RuntimeException(jobExecution + failureExceptions);
            }

            // check stopped
            if(jobExecution.getStatus() == BatchStatus.STOPPED){
                log.warn("== BatchStatus is [{}]", jobExecution.getStatus().name());
                // SIGTERM(143)
                System.exit(143);
            }

        }catch(Exception e){
            log.error("{}", e.getMessage());
            throw e;
        }
    }

    /**
     * printStartupProfile
     */
    private static void printStartupProfile() {
        log.debug("[START] printStartupProfile");
        StartupTimeline startupTimeline = bufferingApplicationStartup.getBufferedTimeline();
        List<StartupTimeline.TimelineEvent> events =  startupTimeline.getEvents();
        for(StartupTimeline.TimelineEvent event : events){
            log.debug("- [{}] {} - {}",
                    String.format("%4s seconds", event.getDuration().getSeconds()),
                    event.getStartupStep().getName(),
                    StreamSupport.stream(event.getStartupStep().getTags().spliterator(), false)
                            .map(tag->String.format("[%s]%s",tag.getKey(),tag.getValue()))
                            .collect(Collectors.joining(","))
            );
        }
        log.debug("[END] printStartupProfile");
    }

    /**
     * printStartupContext
     * @param args
     */
    private static void printStartupContext(String[] args) {
        log.info(StringUtils.repeat("=", 80));
        log.info("= BatchApplication.run");
        log.info("= STAGE:{}", Arrays.toString(applicationContext.getEnvironment().getActiveProfiles()));
        for(int i = 0; i < args.length; i ++ ) {
            log.info("= args[{}]:{}", i, args[i]);
        }
        log.info("= BatchConfig.getDataHome():{}", BatchConfig.getDataHome());
        for(String name : batchContext.getJobParameterNames()) {
            log.info("= batchContext.getJobParameter({}):{}", name, batchContext.getJobParameter(name));
        }
        log.info(StringUtils.repeat("=", 80));
    }

}
