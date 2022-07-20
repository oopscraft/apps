package org.oopscraft.apps.core.bean.definition;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import test.TestComponent;
import test.TestCompositeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class ComponentDefinitionGeneratorTest extends AbstractServiceTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired(required = false)
    private TestCompositeService testComponent;

    @Test
    public void test() {

        // 1. TestMapper must be null
        log.info("== testComponent:{}", testComponent);
        assertNull(testComponent);

        // 2. Creates mapper bean
        ComponentBeanDefinitionFactory definitionGenerator = new ComponentBeanDefinitionFactory();
        BeanDefinition beanDefinition = definitionGenerator.getBeanDefinition(TestComponent.class);
        applicationContext.registerBeanDefinition(TestComponent.class.getName(), beanDefinition);

        // 3. Executes query form mapper bean
        TestComponent testComponent = applicationContext.getBean(TestComponent.class);
        String returnValue = testComponent.getValue("test value");
        log.info("== returnValue:{}", returnValue);
        assertEquals(returnValue, "test value");



//        // generator
//        ComponentDefinitionGenerator componentDefinitionGenerator = new ComponentDefinitionGenerator();
//        MapperDefinitionGenerator mapperDefinitionGenerator = new MapperDefinitionGenerator();
//
//
//        // component
//        applicationContext.registerBeanDefinition(TestComponent.class.getCanonicalName(), componentDefinitionGenerator.generateBeanDefinition(TestComponent.class));
//        applicationContext.registerBeanDefinition(TestChildComponent.class.getCanonicalName(), componentDefinitionGenerator.generateBeanDefinition(TestChildComponent.class));
//
//        // mapper
//        applicationContext.registerBeanDefinition(TestMapper.class.getCanonicalName(), mapperDefinitionGenerator.generateBeanDefinition(TestMapper.class));
//
//        // verify
//        TestComponent testComponent = applicationContext.getBean(TestComponent.class);
//        Assert.notNull(testComponent,"testComponent is null");
//        log.info("testComponent:{}", testComponent);
//        log.info("testComponent.getTestChildComponent:{}", testComponent.getTestChildComponent());
//        log.info("testComponent.getTestChildComponentAutowired:{}", testComponent.getTestChildComponentAutowired());
//        log.info("testComponent.getTestMapper:{}", testComponent.getTestMapper());
//        String selectValue = testComponent.selectValue("test_value");
//        log.info("selectValue:{}", selectValue);
//        String getValue = testComponent.getValue();
//        log.info("getValue:{}", getValue);




//        componentDefinitionGenerator.registerBean(TestComponent.class, applicationContext);
//        componentDefinitionGenerator.registerBean(TestChildComponent.class, applicationContext);
//
//        // creates mapper bean
//        MapperDefinitionGenerator mapperBeanRegister = new MapperDefinitionGenerator();
//        mapperBeanRegister.registerBean(TestMapper.class, applicationContext);
//
//        // verify
//        TestComponent testComponent = applicationContext.getBean(TestComponent.class);
//        Assert.notNull(testComponent,"testComponent is null");
//        log.info("testComponent:{}", testComponent);
//        log.info("testComponent.getTestChildComponent:{}", testComponent.getTestChildComponent());
//        log.info("testComponent.getTestChildComponentAutowired:{}", testComponent.getTestChildComponentAutowired());
//        log.info("testComponent.getTestMapper:{}", testComponent.getTestMapper());
//        String selectValue = testComponent.selectValue("test_value");
//        log.info("selectValue:{}", selectValue);
//        String getValue = testComponent.getValue();
//        log.info("getValue:{}", getValue);
    }

}
