package org.oopscraft.apps.batch.test;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.TestInstance;
import org.oopscraft.apps.batch.BatchConfiguration;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
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

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

@Slf4j
@SpringBootTest(
        classes = {BatchConfiguration.class},
        properties = "spring.main.web-application-type=none"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableTransactionManagement
public class AbstractJobTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    @Getter
    private RoutingDataSource dataSource;

    @Autowired
    @Getter
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Getter
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Getter
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    @Getter
    private JPAQueryFactory jpaQueryFactory;

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

}
