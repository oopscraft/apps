package org.oopscraft.apps.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.job.JobContext;
import org.oopscraft.apps.batch.job.JobContextFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.util.stream.Collectors;

@Slf4j
@Import(BatchConfiguration.class)
@RequiredArgsConstructor
public class BatchApplication implements CommandLineRunner {

    private final JobLauncher jobLauncher;

    private final Job job;

    private static JobContext jobContext;

    /**
     * main
     * @param args
     */
    public static void main(String[] args) throws Exception {

        jobContext = JobContextFactory.getJobContextFromArguments(args);

        new SpringApplicationBuilder(BatchApplication.class)
                .sources(jobContext.getJobClass())
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .web(WebApplicationType.NONE)
                .registerShutdownHook(true)
                .lazyInitialization(true)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        // launch
        JobExecution jobExecution = jobLauncher.run(job, jobContext.getJobParameters());
        if(jobExecution.getStatus().isUnsuccessful()){
            String failureExceptions = jobExecution.getFailureExceptions().stream()
                    .map(e -> e.getMessage())
                    .collect(Collectors.joining());
            log.error("failureExceptions:{}", failureExceptions);
            throw new RuntimeException(jobExecution + failureExceptions);
        }
    }

}
