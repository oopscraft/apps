package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;


@EnableConfigurationProperties(BatchConfig.class)
@ConfigurationPropertiesScan
@Import({
        CoreConfiguration.class,
        BatchConfiguration.SpringBatchConfiguration.class
})
public class BatchConfiguration implements EnvironmentPostProcessor, BeanFactoryPostProcessor {


    @Setter
    private static BatchContext batchContext = new BatchContext();

    /**
     * postProcessEnvironment
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Arrays.stream(environment.getActiveProfiles())
                .sorted(Comparator.reverseOrder())
                .forEach(profile -> {
                    String location = String.format("classpath:batch-config-%s.yml", profile);
                    environment.getPropertySources().addLast(parseYamlResource(location));
                });
        environment.getPropertySources().addLast(parseYamlResource(("classpath:batch-config.yml")));
    }

    /**
     * parseYamResource
     * @param location
     * @return
     */
    private PropertySource parseYamlResource(String location) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        if(resource.exists()) {
            YamlPropertiesFactoryBean yamlPropertiesFactory = new YamlPropertiesFactoryBean();
            yamlPropertiesFactory.setResources(resource);
            yamlPropertiesFactory.afterPropertiesSet();
            return new PropertiesPropertySource(location, yamlPropertiesFactory.getObject());
        }else{
            return new PropertiesPropertySource(location, new Properties());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerSingleton(BatchContext.class.getCanonicalName(), batchContext);
    }

    /**
     * custom batch configuration
     */
    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    public static class SpringBatchConfiguration implements InitializingBean, DisposableBean {

        private static final String META_TABLE_PREFIX = "SPRING_BATCH_";

        private static StepScope stepScope;

        private static JobScope jobScope;

        static {
            jobScope = new JobScope();
            jobScope.setAutoProxy(false);
            stepScope = new StepScope();
            stepScope.setAutoProxy(false);
        }

        private final BatchConfig batchConfig;

        private final PlatformTransactionManager transactionManager;

        private HikariDataSource batchDataSource;

        private PlatformTransactionManager batchTransactionManager;

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
                batchTransactionManager = new DataSourceTransactionManager(batchDataSource);
            }else {
                batchDataSource = null;
                batchTransactionManager = new ResourcelessTransactionManager();
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
                JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
                jobRepositoryFactoryBean.setDataSource(batchDataSource);
                jobRepositoryFactoryBean.setTransactionManager(batchTransactionManager);
                jobRepositoryFactoryBean.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
                jobRepositoryFactoryBean.setTablePrefix(META_TABLE_PREFIX);
                jobRepositoryFactoryBean.setMaxVarCharLength(1024);
                jobRepositoryFactoryBean.afterPropertiesSet();
                return jobRepositoryFactoryBean.getObject();
            }
            // none-resource TransactionManager
            else{
                MapJobRepositoryFactoryBean jobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(batchTransactionManager);
                jobRepositoryFactoryBean.afterPropertiesSet();
                return jobRepositoryFactoryBean.getObject();
            }
        }

        @Bean
        public JobExplorer jobExplorer(JobRepository jobRepository) throws Exception {
            if(batchDataSource != null) {
                JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
                jobExplorerFactoryBean.setDataSource(batchDataSource);
                jobExplorerFactoryBean.setTablePrefix(META_TABLE_PREFIX);
                jobExplorerFactoryBean.afterPropertiesSet();
                return jobExplorerFactoryBean.getObject();
            }else{
                MapJobExplorerFactoryBean jobExplorerFactoryBean = new MapJobExplorerFactoryBean();
                jobExplorerFactoryBean.setRepositoryFactory(new MapJobRepositoryFactoryBean(batchTransactionManager));
                jobExplorerFactoryBean.afterPropertiesSet();
                return jobExplorerFactoryBean.getObject();
            }
        }

        @Bean
        public JobRegistry jobRegistry() {
            return new MapJobRegistry();
        }

        @Bean
        public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
            SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(jobRepository);
            jobLauncher.afterPropertiesSet();
            return jobLauncher;
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
