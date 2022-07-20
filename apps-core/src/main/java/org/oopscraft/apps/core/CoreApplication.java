package org.oopscraft.apps.core;

import org.oopscraft.apps.core.bean.BeanInitializer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

@Import(CoreConfiguration.class)
public class CoreApplication implements ApplicationRunner {

    static ConfigurableApplicationContext applicationContext;

    /**
     * main
     * @param args
     */
    public static void main(String[] args) throws Exception {
        applicationContext = new SpringApplicationBuilder(CoreApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .web(WebApplicationType.NONE)
                .registerShutdownHook(true)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BeanInitializer.initializeBean(applicationContext, null);
    }
}
