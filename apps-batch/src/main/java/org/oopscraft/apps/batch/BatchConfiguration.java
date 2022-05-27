package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Import({
        CoreConfiguration.class,
        BatchConfiguration.CustomBatchConfiguration.class
})
public class BatchConfiguration {

    /**
     * batchConfig
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "batch")
    private BatchConfig batchConfig() {
        return new BatchConfig();
    }

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
            if(batchConfig.getDatasource() != null){
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
            // false 일 경우는 none-resource TransactionManager
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


//    /**
//     * BatchConfigurer
//     */
//    @EnableBatchProcessing
//    @RequiredArgsConstructor
//    public static class BatchConfigurer implements org.springframework.batch.core.configuration.annotation.BatchConfigurer, InitializingBean, DisposableBean {
//
//        private final BatchConfig batchConfig;
//
//        private HikariDataSource batchDataSource;
//
//        private PlatformTransactionManager batchTransactionManager;
//
//        private JobRepository jobRepository;
//
//        private SimpleJobLauncher jobLauncher;
//
//        @Override
//        public void afterPropertiesSet() throws Exception {
//            if(batchConfig.getDatasource() != null){
//                batchDataSource = new HikariDataSource(batchConfig.getDatasource());
//                batchTransactionManager = new DataSourceTransactionManager(batchDataSource);
//                JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
//                jobRepositoryFactoryBean.setDataSource(batchDataSource);
//                jobRepositoryFactoryBean.setTransactionManager(batchTransactionManager);
//                jobRepositoryFactoryBean.setTablePrefix("SPRING_BATCH_");
//                jobRepositoryFactoryBean.setMaxVarCharLength(1024);
//                jobRepositoryFactoryBean.afterPropertiesSet();
//                jobRepository = jobRepositoryFactoryBean.getObject();
//            }else{
//                batchTransactionManager = new ResourcelessTransactionManager();
//                MapJobRepositoryFactoryBean jobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(batchTransactionManager);
//                jobRepositoryFactoryBean.afterPropertiesSet();
//                jobRepository = jobRepositoryFactoryBean.getObject();
//            }
//
//            jobLauncher = new SimpleJobLauncher();
//            jobLauncher.setJobRepository(jobRepository);
//            jobLauncher.afterPropertiesSet();
//        }
//
//        @Override
//        public void destroy() throws Exception {
//            if(batchDataSource != null) {
//                batchDataSource.close();
//            }
//        }
//
//        @Override
//        public JobRepository getJobRepository() throws Exception {
//            return jobRepository;
//        }
//
//        @Override
//        public PlatformTransactionManager getTransactionManager() throws Exception {
//            return batchTransactionManager;
//        }
//
//        @Override
//        public JobLauncher getJobLauncher() throws Exception {
//            return jobLauncher;
//        }
//
//        @Override
//        public JobExplorer getJobExplorer() throws Exception {
//            return null;
//        }
//
//   }

}
