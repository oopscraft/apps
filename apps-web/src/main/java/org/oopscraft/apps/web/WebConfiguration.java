package org.oopscraft.apps.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.CoreConfiguration;
import org.oopscraft.apps.core.data.PageRequestArgumentResolver;
import org.oopscraft.apps.web.security.AuthenticationFilter;
import org.oopscraft.apps.web.security.AuthenticationHandler;
import org.oopscraft.apps.web.security.AuthenticationProvider;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
@Import({CoreConfiguration.class})
public class WebConfiguration implements EnvironmentPostProcessor, WebMvcConfigurer {

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
                    String location = String.format("classpath:web-config-%s.yml", profile);
                    environment.getPropertySources().addLast(parseYamlResource(location));
                });
        environment.getPropertySources().addLast(parseYamlResource(("classpath:web-config.yml")));
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
     * webConfig
     * @return
     * @throws Exception
     */
    @Bean
    @ConfigurationProperties(prefix = "web")
    public WebConfig webConfig() throws Exception {
        return new WebConfig();
    }

    /**
     * addInterceptors
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("_locale");
        registry.addInterceptor(localeChangeInterceptor);
    }

    /**
     * addArgumentResolvers
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageRequestArgumentResolver pageableArgumentResolver = new PageRequestArgumentResolver();
        argumentResolvers.add(pageableArgumentResolver);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>(){
                    @Override
                    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
                        gen.writeNumber(date.getTime());
                    }
                })
                .deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                        Date date = new Date(p.getValueAsLong());
                        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    }
                })
                .serializerByType(LocalDate.class, new JsonSerializer<LocalDate>(){
                    @Override
                    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        Date date = new Date(java.sql.Date.valueOf(value).getTime());
                        gen.writeNumber(date.getTime());
                    }
                })
                .deserializerByType(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                        Date date = new Date(p.getValueAsLong());
                        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                });
    }

    /**
     * csrfTokenRepository
     * @return
     */
    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieName("X-Csrf-Token");
        csrfTokenRepository.setHeaderName("X-Csrf-Token");
        return csrfTokenRepository;
    }

    /**
     * REST API security configuration
     * base on HTTP authorization header
     */
    @Order(1)
    @RequiredArgsConstructor
    public class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final AuthenticationProvider authenticationProvider;

        private final AuthenticationFilter authenticationFilter;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll();
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.csrf().disable();
            http.authenticationProvider(authenticationProvider);
            http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    /**
     * BaseSecurityConfiguration
     */
    @Order(99)
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
    @RequiredArgsConstructor
    public static class BaseSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private final WebConfig webConfig;

        private final AuthenticationProvider authenticationProvider;

        private final AuthenticationHandler authenticationHandler;

        private final AuthenticationFilter authenticationFilter;

        private final CookieCsrfTokenRepository csrfTokenRepository;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            log.info("CoreSecurityConfiguration.configure");

            // set default anonymous authorities
            List<String> anonymousAuthorities = new ArrayList<String>(){{
                add("ROLE_ANONYMOUS");
                addAll(webConfig.getDefaultAuthorities());
            }};
            http.anonymous().principal("anonymous").authorities(anonymousAuthorities.toArray(new String[0]));

            // rules
            http.antMatcher("/**")
                    .authorizeRequests()
                    .anyRequest().permitAll();

            // provider, session
            http.authenticationProvider(authenticationProvider);
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.csrf().csrfTokenRepository(csrfTokenRepository);

            // filter
            http.addFilterAfter(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

            // exception handler
            http.exceptionHandling().authenticationEntryPoint(authenticationHandler);

            // login
            http.formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginProcessingUrl("/login/process")
                    .successHandler(authenticationHandler)
                    .failureHandler(authenticationHandler)
                    .permitAll();

            // logout
            http.logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .logoutSuccessHandler(authenticationHandler)
                    .invalidateHttpSession(true)
                    .deleteCookies(AuthenticationHandler.ACCESS_TOKEN_HEADER_NAME)
                    .permitAll();
        }
    }

}
