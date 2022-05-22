package org.oopscraft.apps.web;

import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAutoConfiguration
@ComponentScan
@Import({CoreConfiguration.class})
public class WebConfiguration implements WebMvcConfigurer {


}
