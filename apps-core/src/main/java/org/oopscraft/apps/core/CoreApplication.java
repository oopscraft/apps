package org.oopscraft.apps.core;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;

@Import(CoreConfiguration.class)
public class CoreApplication {

    /**
     * main
     * @param args
     */
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(CoreApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .web(WebApplicationType.NONE)
                .registerShutdownHook(true)
                .run(args);
    }

}
