package org.oopscraft.apps.web;

import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAutoConfiguration
@ComponentScan
@Import({CoreConfiguration.class})
public class WebConfiguration implements EnvironmentPostProcessor, WebMvcConfigurer {


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

    }
}
