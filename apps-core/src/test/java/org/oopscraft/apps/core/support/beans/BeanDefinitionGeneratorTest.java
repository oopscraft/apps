package org.oopscraft.apps.core.support.beans;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import sandbox.TestComponent;
import sandbox.TestCompositeService;
import sandbox.TestMapper;
import sandbox.TestStaticBlockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanDefinitionGeneratorTest extends AbstractServiceTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Test
    public void testDefault() throws Exception {

        // 1. mapper 생성
        BeanDefinition mapperDefinition = BeanDefinitionGenerator.generateBeanDefinition(TestMapper.class);
        applicationContext.registerBeanDefinition(TestMapper.class.getName(), mapperDefinition);

        // 2. component 생성
        BeanDefinition componentDefinition = BeanDefinitionGenerator.generateBeanDefinition(TestComponent.class);
        applicationContext.registerBeanDefinition(TestComponent.class.getName(), componentDefinition);

        // 3. composite service 생성
        BeanDefinition compositeServiceDefinition = BeanDefinitionGenerator.generateBeanDefinition(TestCompositeService.class);
        applicationContext.registerBeanDefinition(TestCompositeService.class.getName(), compositeServiceDefinition);

        // 4. call test
        TestCompositeService compositeService = applicationContext.getBean(TestCompositeService.class);
        assertEquals(compositeService.selectValue("test_value"),"test_value");
        assertEquals(compositeService.getValue("test_value"), "test_value");

    }

    @Test
    public void testStaticBlockBean() throws Exception {
        BeanDefinition beanDefinition = BeanDefinitionGenerator.generateBeanDefinition(TestStaticBlockBean.class.getName());
        applicationContext.registerBeanDefinition(TestStaticBlockBean.class.getName(), beanDefinition);

    }
}
