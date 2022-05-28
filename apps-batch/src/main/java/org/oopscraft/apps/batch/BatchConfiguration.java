package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


@EnableConfigurationProperties(BatchConfig.class)
@ConfigurationPropertiesScan
@Import({
        CoreConfiguration.class,
        BatchConfiguration.CustomBatchConfiguration.class
})
public class BatchConfiguration {

    /**
     * CustomBatchConfiguration
     * spring meta data 는 업무 도메인 DBMS 와 별도로 구성 됨으로 기존 @EnableBatchProcess 및 BatchConfigurer 구성을 할수 없음
     */
    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    public static class CustomBatchConfiguration implements InitializingBean, DisposableBean {

        private final BatchConfig batchConfig;

        private final PlatformTransactionManager transactionManager;

        private HikariDataSource batchDataSource;

        private static StepScope stepScope;

        private static JobScope jobScope;

        static {
            jobScope = new JobScope();
            jobScope.setAutoProxy(false);
            stepScope = new StepScope();
            stepScope.setAutoProxy(false);
        }

        @Bean
        public static StepScope stepScope() {
            return stepScope;
        }

        @Bean
        public static JobScope jobScope() {
            return jobScope;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            if(batchConfig.isEnableDatasource()){
                batchDataSource = new HikariDataSource(batchConfig.getDatasource());
            }
        }

        @Override
        public void destroy() throws Exception {
            if(batchDataSource != null) {
                batchDataSource.close();
            }
        }

        @Bean
        public JobRepository jobRepository() throws Exception {
            // Job Repository with datasource
            if(batchDataSource != null){
                DataSourceTransactionManager batchTransactionManager = new DataSourceTransactionManager(batchDataSource);
                JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
                jobRepositoryFactoryBean.setDataSource(batchDataSource);
                jobRepositoryFactoryBean.setTransactionManager(batchTransactionManager);
                jobRepositoryFactoryBean.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
                jobRepositoryFactoryBean.setTablePrefix("SPRING_BATCH_");
                jobRepositoryFactoryBean.setMaxVarCharLength(1024);
                return jobRepositoryFactoryBean.getObject();
            }
            // none-resource TransactionManager
            else{
                PlatformTransactionManager batchTransactionManager = new ResourcelessTransactionManager();
                MapJobRepositoryFactoryBean jobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(batchTransactionManager);
                return jobRepositoryFactoryBean.getObject();
            }
        }

        @Bean
        public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
            SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(jobRepository);
            jobLauncher.afterPropertiesSet();
            return jobLauncher;
        }

        @Bean
        public JobExplorer jobExplorer() throws Exception {
            throw new IllegalAccessException();
        }

        @Bean
        public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository){
            return new JobBuilderFactory(jobRepository);
        }

       @Bean
        public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository) {
            return new StepBuilderFactory(jobRepository, transactionManager);
        }
    }

}
