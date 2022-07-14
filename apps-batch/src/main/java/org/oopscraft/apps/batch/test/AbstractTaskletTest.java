package org.oopscraft.apps.batch.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfiguration;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@Deprecated
@SpringBootTest(
        classes = {BatchConfiguration.class},
        properties = "spring.main.web-application-type=none"
)
@Slf4j
public class AbstractTaskletTest {
    @Autowired
    @Getter
    private ConfigurableApplicationContext applicationContext;

    /**
     * launchTasklet
     * @param tasklet
     * @param batchContext
     */
    public final void launchTasklet(AbstractTasklet tasklet, BatchContext batchContext) {
        ConfigurableListableBeanFactory beanFactory = null;
        try {
            beanFactory = applicationContext.getBeanFactory();

            // add batch context
            batchContext.setJobParameter("_", UUID.randomUUID().toString());
            batchContext.setJobClass(tasklet.getClass());
            log.info("BatchContext: {}", batchContext);
            beanFactory.registerSingleton("batchContext", batchContext);

            // creates dummy job
            AbstractJob job = new AbstractJob() {
               @Override
                public void initialize(BatchContext batchContext) {
                    addTasklet(tasklet);
                }
            };
            beanFactory.initializeBean(job, "job");

            // launches job
            JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
            JobExecution jobExecution = jobLauncher.run(job, batchContext.getJobParameters());
            if(jobExecution.getStatus() != BatchStatus.COMPLETED){
                throw new RuntimeException("jobExecution Not Completed.");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            try {
                ((DefaultListableBeanFactory) beanFactory).destroySingleton("batchContext");
            }catch(Exception ignore){}
            try {
                ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition("job");
            }catch(Exception ignore){}
        }
    }

    /**
     * launchTaskletWithDataSourceKey
     * @param tasklet
     * @param batchContext
     * @param dataSourceKey
     */
    public final void launchTaskletWithDataSourceKey(AbstractTasklet tasklet, BatchContext batchContext, String dataSourceKey) {
        tasklet.setDataSourceKey(dataSourceKey);
        launchTasklet(tasklet, batchContext);
    }

}
