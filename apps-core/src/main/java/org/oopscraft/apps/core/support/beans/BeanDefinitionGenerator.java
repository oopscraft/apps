package org.oopscraft.apps.core.support.beans;

import org.springframework.beans.factory.config.BeanDefinition;

public class BeanDefinitionGenerator {

    /**
     * generateBeanDefinition
     * @param beanClass
     * @return
     */
    public static BeanDefinition generateBeanDefinition(Class<?> beanClass) throws UnsupportedBeanTypeException {
        BeanDefinitionFactory beanDefinitionFactory = BeanDefinitionFactory.getBeanDefinitionFactory(beanClass);
        return beanDefinitionFactory.getBeanDefinition(beanClass);
    }

    /**
     * generateBeanDefinition
     * @param beanClassName
     * @return
     * @throws UnsupportedBeanTypeException
     * @throws ClassNotFoundException
     */
    public static BeanDefinition generateBeanDefinition(String beanClassName) throws UnsupportedBeanTypeException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> beanClass = Class.forName(beanClassName, false, classLoader);
        return generateBeanDefinition(beanClass);
    }




}
