package org.oopscraft.apps.batch.test;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.oopscraft.apps.batch.BatchConfiguration;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

@Slf4j
@SpringBootTest(
        classes = {BatchConfiguration.class}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableTransactionManagement
public class AbstractJobTest {

    @Autowired
    protected GenericApplicationContext applicationContext;

    @Autowired
    @Getter
    protected RoutingDataSource dataSource;

    @Autowired
    @Getter
    protected PlatformTransactionManager transactionManager;

    @Autowired
    @Getter
    protected SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Getter
    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    @Getter
    protected JPAQueryFactory jpaQueryFactory;

    @Autowired
    @Getter
    protected JobLauncher jobLauncher;

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
            batchContextBean.setJobParameter("_junit", UUID.randomUUID().toString());

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
     * TaskletTestJob
     */
    public static class TaskletTestJob extends AbstractJob {
        @Setter
        public static Tasklet tasklet = null;
        @Override
        public void initialize(BatchContext batchContext) {
            addStep(tasklet);
        }
    }

    /**
     * runTasklet
     * @param tasklet
     */
    public final StepExecution runTasklet(Tasklet tasklet) {
        TaskletTestJob.setTasklet(tasklet);
        BatchContext batchContext = BatchContext.builder()
                .jobClass(TaskletTestJob.class)
                .baseDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .jobParameter("_junit", UUID.randomUUID().toString())
                .build();
        JobExecution jobExecution = runJob(batchContext);
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        for(StepExecution stepExecution : stepExecutions){
            return stepExecution;
        }
        return null;
    }

}
