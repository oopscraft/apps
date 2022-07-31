package org.oopscraft.apps.core;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.type.JdbcType;
import org.hibernate.cfg.AvailableSettings;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.oopscraft.apps.AppsBasePackage;
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
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@EnableAutoConfiguration
@EnableConfigurationProperties(CoreConfig.class)
@EnableTransactionManagement
@ComponentScan(
        basePackageClasses = {AppsBasePackage.class},
        basePackages = "${core.base-packages}",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableJpaRepositories(
        basePackageClasses = {AppsBasePackage.class},
        entityManagerFactoryRef = "entityManagerFactory"
)
@EntityScan(
        basePackageClasses = {AppsBasePackage.class}
)
@MapperScan(
        basePackageClasses = {AppsBasePackage.class},
        basePackages = "${core.base-packages}",
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


        // EnableJpaRepositories 는 SpEL 적용이 안됨 으로 직접 변경 (해당 부분 JDK warning 발생함)
        EnableJpaRepositories annotations = this.getClass().getDeclaredAnnotation(EnableJpaRepositories.class);
        if(java.lang.reflect.Proxy.isProxyClass(annotations.getClass())){
            try {
                java.lang.reflect.InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(annotations);
                Class<?> handlerClass = handler.getClass();
                Field memberValues = handlerClass.getDeclaredField("memberValues");
                memberValues.setAccessible(true);
                Map<Object,Object> annotationAttributes = (Map<Object,Object>) memberValues.get(handler);
                String coreBasePackages = environment.getProperty("core.base-packages");
                if(coreBasePackages != null && coreBasePackages.trim().length() > 0) {
                    String[] basePackages = (String[])annotationAttributes.get("basePackages");
                    Arrays.stream(basePackages).map(element -> {
                        return environment.resolvePlaceholders(element);
                    }).toArray(unused -> basePackages);
                    annotationAttributes.put("basePackages", basePackages);
                }else{
                    annotationAttributes.put("basePackages", new String[]{});
                }
                memberValues.set(handler, annotationAttributes);
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }else{
            log.warn("== Scans All Jpa Repository");
        }

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
        String defaultKey = coreConfig.getDatasourceKey();
        Map<String, HikariConfig> dataSources = coreConfig.getDatasource();
        Map<Object, Object> targetDataSources = new LinkedHashMap<Object, Object>();
        int count = 0;
        for (String key : dataSources.keySet()) {
            count ++;
            HikariConfig hikariConfig = dataSources.get(key);
            log.debug(StringUtils.repeat("-", 80));
            log.debug("- DataSource[{}]: {} {}", count, key, (key.equals(defaultKey)?"[*]":""));
            log.debug("- driver: {}", hikariConfig.getDriverClassName());
            log.debug("- jdbcUrl: {}", hikariConfig.getJdbcUrl());
            log.debug("- username: {}", hikariConfig.getUsername());
            log.debug("- connectionTestQuery: {}", hikariConfig.getConnectionTestQuery());
            log.debug("- initializationFailTimeout: {}", hikariConfig.getInitializationFailTimeout());
            log.debug("- minimumIdle: {}", hikariConfig.getMinimumIdle());
            log.debug("- maximumPoolSize: {}", hikariConfig.getMaximumPoolSize());
            log.debug("- idleTimeout: {}", hikariConfig.getIdleTimeout());
            log.debug("- maxLifetime: {}", hikariConfig.getMaxLifetime());
            HikariDataSource targetDataSource = new HikariDataSource(hikariConfig);
            targetDataSources.put(key, targetDataSource);
            log.debug(StringUtils.repeat("-", 80));
        }
        return new RoutingDataSource(defaultKey, targetDataSources);
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
        entityManagerFactory.setPackagesToScan(AppsBasePackage.class.getPackage().getName());

        // defines vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty(AvailableSettings.HQL_BULK_ID_STRATEGY, org.hibernate.hql.spi.id.inline.InlineIdsOrClauseBulkIdStrategy.class.getName());    // Bulk-id strategies when you can’t use temporary tables
        jpaProperties.setProperty(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.SHOW_SQL, Boolean.toString(log.isDebugEnabled()));
        jpaProperties.setProperty(AvailableSettings.FORMAT_SQL, Boolean.toString(log.isDebugEnabled()));
        jpaProperties.setProperty(AvailableSettings.USE_SQL_COMMENTS, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, Boolean.toString(true));

        // cache configuration
        jpaProperties.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.USE_QUERY_CACHE, Boolean.toString(true));
        jpaProperties.setProperty(AvailableSettings.CACHE_REGION_FACTORY, org.hibernate.cache.jcache.internal.JCacheRegionFactory.class.getName());

        if(EmbeddedDatabaseConnection.isEmbedded(dataSource)) {
            vendorAdapter.setGenerateDdl(true);
            jpaProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, "create");
            jpaProperties.setProperty(AvailableSettings.HBM2DDL_IMPORT_FILES, "/db/data.sql");
            jpaProperties.setProperty(AvailableSettings.HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR, org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor.class.getName());
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
     * @param entityManager
     * @return
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
       return new JPAQueryFactory(entityManager);
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
