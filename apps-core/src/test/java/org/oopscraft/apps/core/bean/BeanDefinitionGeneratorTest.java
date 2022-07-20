package org.oopscraft.apps.core.bean;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import test.TestComponent;
import test.TestCompositeService;
import test.TestMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeanDefinitionGeneratorTest extends AbstractServiceTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Test
    public void testDefault() throws Exception {

        // 1. mapper 생성
        BeanDefinition mapperDefinition = BeanDefinitionGenerator.getBeanDefinition(TestMapper.class);
        applicationContext.registerBeanDefinition(TestMapper.class.getName(), mapperDefinition);

        // 2. component 생성
        BeanDefinition componentDefinition = BeanDefinitionGenerator.getBeanDefinition(TestComponent.class);
        applicationContext.registerBeanDefinition(TestComponent.class.getName(), componentDefinition);

        // 3. composite service 생성
        BeanDefinition compositeServiceDefinition = BeanDefinitionGenerator.getBeanDefinition(TestCompositeService.class);
        applicationContext.registerBeanDefinition(TestCompositeService.class.getName(), compositeServiceDefinition);

        // 4. call test
        TestCompositeService compositeService = applicationContext.getBean(TestCompositeService.class);
        assertEquals(compositeService.selectValue("test_value"),"test_value");
        assertEquals(compositeService.getValue("test_value"), "test_value");

    }
}
