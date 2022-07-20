package org.oopscraft.apps.core.bean;

import org.oopscraft.apps.core.bean.definition.AbstractBeanDefinitionFactory;
import org.springframework.beans.factory.config.BeanDefinition;

public class BeanDefinitionGenerator {

    /**
     * generateBeanDefinition
     * @param beanClass
     * @return
     */
    public static BeanDefinition getBeanDefinition(Class<?> beanClass) throws UnsupportedBeanTypeException {
        AbstractBeanDefinitionFactory beanDefinitionFactory = AbstractBeanDefinitionFactory.getBeanDefinitionFactory(beanClass);
        return beanDefinitionFactory.getBeanDefinition(beanClass);
    }
}
