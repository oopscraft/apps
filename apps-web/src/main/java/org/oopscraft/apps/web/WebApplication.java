package org.oopscraft.apps.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.Import;

@Import(WebConfiguration.class)
public class WebApplication {

    /**
     * main
     * @param args
     */
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(WebApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }

}
