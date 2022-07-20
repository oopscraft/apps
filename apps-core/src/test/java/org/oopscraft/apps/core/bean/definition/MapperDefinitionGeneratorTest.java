package org.oopscraft.apps.core.bean.definition;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import test.TestMapper;
import org.oopscraft.apps.core.test.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class MapperDefinitionGeneratorTest extends AbstractServiceTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired(required = false)
    TestMapper testMapper;

    @Test
    public void test() {

        // 1. TestMapper must be null
        log.info("== testMapper:{}", testMapper);
        assertNull(testMapper);

        // 2. Creates mapper bean
        MapperBeanDefinitionFactory definitionGenerator = new MapperBeanDefinitionFactory();
        BeanDefinition beanDefinition = definitionGenerator.getBeanDefinition(TestMapper.class);
        applicationContext.registerBeanDefinition(TestMapper.class.getName(), beanDefinition);

        // 3. Executes query form mapper bean
        TestMapper testMapper = applicationContext.getBean(TestMapper.class);
        String returnValue = testMapper.selectValue("test value");
        log.info("== returnValue:{}", returnValue);
        assertEquals(returnValue, "test value");
    }

}