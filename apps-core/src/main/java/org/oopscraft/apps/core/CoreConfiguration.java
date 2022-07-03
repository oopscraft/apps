package org.oopscraft.apps.core;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.type.JdbcType;
import org.hibernate.cfg.AvailableSettings;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.oopscraft.apps.AppsPackage;
import org.oopscraft.apps.core.data.RoutingDataSource;
import org.oopscraft.apps.core.message.MessageService;
import org.oopscraft.apps.core.message.MessageSource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.*;

@Slf4j
@EnableAutoConfiguration
@EnableConfigurationProperties(CoreConfig.class)
@EnableTransactionManagement
@ComponentScan(
        basePackageClasses = {AppsPackage.class},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableJpaRepositories(
        basePackageClasses = {AppsPackage.class},
        entityManagerFactoryRef = "entityManagerFactory"
)
@EntityScan(
        basePackageClasses = {AppsPackage.class}
)
@MapperScan(
        basePackageClasses = {AppsPackage.class},
        annotationClass = Mapper.class,
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        sqlSessionFactoryRef = "sqlSessionFactory"
)
public class CoreConfiguration implements EnvironmentPostProcessor {

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
                    String location = String.format("classpath:core-config-%s.yml", profile);
                    environment.getPropertySources().addLast(parseYamlResource(location));
                });
        environment.getPropertySources().addLast(parseYamlResource(("classpath:core-config.yml")));
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

    /**
     * dataSource
     * @param coreConfig
     * @return
     * @throws Exception
     */
    @Bean(destroyMethod = "close")
    public RoutingDataSource dataSource(CoreConfig coreConfig) throws Exception {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<String, HikariConfig> datasource = coreConfig.getDatasource();
        Map<Object, Object> targetDataSources = new LinkedHashMap<Object, Object>();
        for(String key: datasource.keySet()) {
            HikariDataSource targetDataSource = new HikariDataSource(datasource.get(key));
            targetDataSources.put(key, targetDataSource);
        }
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(targetDataSources.get(RoutingDataSource.DEFAULT_KEY));
        return routingDataSource;
    }

    /**
     * entityManagerFactory
     * @param dataSource
     * @param coreConfig
     * @return
     * @throws Exception
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, CoreConfig coreConfig) throws Exception {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);

        // package to scan
        entityManagerFactory.setPackagesToScan(AppsPackage.class.getPackage().getName());

        // defines vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty(AvailableSettings.HQL_BULK_ID_STRATEGY, org.hibernate.hql.spi.id.inline.InlineIdsOrClauseBulkIdStrategy.class.getName());    // Bulk-id strategies when you can’t use temporary tables
        jpaProperties.setProperty(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.SHOW_SQL, Boolean.toString(log.isDebugEnabled()));
        jpaProperties.setProperty(AvailableSettings.FORMAT_SQL, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.USE_SQL_COMMENTS, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, Boolean.toString(true));

        // cache configuration
        jpaProperties.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.USE_QUERY_CACHE, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.CACHE_REGION_FACTORY, org.hibernate.cache.jcache.internal.JCacheRegionFactory.class.getName());

        if(EmbeddedDatabaseConnection.isEmbedded(dataSource)) {
            vendorAdapter.setGenerateDdl(true);
        }

        // return
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource, LocalContainerEntityManagerFactoryBean entityManagerFactory) throws Exception {
        Assert.notNull(entityManagerFactory, "entityManagerFactory must not be null.");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }

    /**
     * sqlSessionFactory
     * @param dataSource
     * @param coreConfig
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, CoreConfig coreConfig) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        // sets configurations
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(Slf4jImpl.class);
        configuration.setVfsImpl(SpringBootVFS.class);

        // enable lazy loading
        configuration.setLazyLoadingEnabled(true);
        configuration.setAggressiveLazyLoading(false);
        configuration.getLazyLoadTriggerMethods().clear();

        // returns
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean;
    }

    /**
     * jpaQueryFactory
     * @param entityManagerFactory
     * @return
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManagerFactory entityManagerFactory) {
       return new JPAQueryFactory(entityManagerFactory.createEntityManager());
    }

    /**
     * messageSource
     * @param messageService
     * @return
     */
    @Bean
    public MessageSource messageSource(MessageService messageService) {
        MessageSource messageSource = new MessageSource(messageService);
        messageSource.addBasenames("classpath*:/message/messages");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(10);
        return messageSource;
    }

}
