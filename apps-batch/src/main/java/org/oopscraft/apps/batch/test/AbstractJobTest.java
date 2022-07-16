package org.oopscraft.apps.batch.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oopscraft.apps.batch.BatchConfiguration;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

@Slf4j
@SpringBootTest(
        classes = {BatchConfiguration.class},
        properties = "spring.main.web-application-type=none"
)
//@SpringBatchTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractJobTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    @Getter
    private JobLauncher jobLauncher;

    /**
     * runJob
     * @param batchContext
     * @return
     */
    public final JobExecution runJob(BatchContext batchContext) {
        Class<? extends Job> jobClass = batchContext.getJobClass();
        AutowireCapableBeanFactory beanFactory = applicationContext.getBeanFactory();
        AbstractJob job = null;
        try {
            // setting batch context
            BatchContext batchContextBean = applicationContext.getBean(BatchContext.class);
            batchContextBean.setJobClass(batchContext.getJobClass());
            batchContextBean.setBaseDate(batchContext.getBaseDate());
            batchContextBean.setJobParameters(batchContext.getJobParameters());

            // initializes job
            try {
                job = (AbstractJob) beanFactory.getBean(jobClass);
            }catch(NoSuchBeanDefinitionException e) {
                job = (AbstractJob) beanFactory.autowire(jobClass, AUTOWIRE_CONSTRUCTOR, true);
                beanFactory.autowireBean(job);
                job.setApplicationContext(applicationContext);
                beanFactory.initializeBean(job, jobClass.getName());
            }

            // launches job
            return jobLauncher.run(job, batchContext.createJobParameters());
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            try {
                ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition(jobClass.getName());
            }catch(Exception ignore){
                log.warn(ignore.getMessage());
            }
        }
    }





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
//            batchContext.setJobClass(tasklet.getClass());
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
            JobExecution jobExecution = jobLauncher.run(job, batchContext.createJobParameters());
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
