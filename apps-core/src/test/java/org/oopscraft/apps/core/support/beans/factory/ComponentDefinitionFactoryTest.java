package org.oopscraft.apps.core.support.beans.factory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import sandbox.TestComponent;
import sandbox.TestCompositeService;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ComponentDefinitionFactoryTest extends AbstractServiceTest {

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
        ComponentDefinitionFactory beanDefinitionFactory = new ComponentDefinitionFactory();
        assertEquals(beanDefinitionFactory.support(TestComponent.class), true);
        BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinition(TestComponent.class);
        applicationContext.registerBeanDefinition(TestComponent.class.getName(), beanDefinition);

        // 3. Executes query form mapper bean
        TestComponent testComponent = applicationContext.getBean(TestComponent.class);
        String returnValue = testComponent.getValue("test value");
        log.info("== returnValue:{}", returnValue);
        assertEquals(returnValue, "test value");
    }

    @Test
    public void testStaticBlock() throws Exception {
        try {
            Class.forName("sandbox.TestComponent");

        }catch(Exception e){
            assertTrue(true, "ok");
        }
    }

   @Test
    public void skipStaticBlock() throws Exception {
        Class.forName("sandbox.TestComponent", false, this.getClass().getClassLoader());
        log.info("nothing");
    }


}
