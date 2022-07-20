package org.oopscraft.apps.core.bean.definition;

import org.oopscraft.apps.core.bean.UnsupportedBeanTypeException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBeanDefinitionFactory {

    /**
     * factory registry
     */
    private static Set<AbstractBeanDefinitionFactory> beaDefinitionFactoryRegistry = new HashSet<AbstractBeanDefinitionFactory>(){{
        add(new ComponentBeanDefinitionFactory());
        add(new MapperBeanDefinitionFactory());
        add(new RepositoryDefinitionFactory());
        add(new ControllerBeanDefinitionFactory());
    }};

    /**
     * gets bean definition factory
     * @param beanClass
     * @return
     * @throws UnsupportedBeanTypeException
     */
    public static AbstractBeanDefinitionFactory getBeanDefinitionFactory(Class<?> beanClass) throws UnsupportedBeanTypeException {
        for(AbstractBeanDefinitionFactory beanDefinitionFactory : beaDefinitionFactoryRegistry){
            if(beanDefinitionFactory.support(beanClass)){
                return beanDefinitionFactory;
            }
        }
        throw new UnsupportedBeanTypeException(beanClass);
    }

    /**
     * support
     * @param beanClass
     * @return
     */
    public abstract boolean support(Class<?> beanClass);

    /**
     * gets bean definition
     * @param beanClass candidate bean class
     * @return
     */
    public abstract BeanDefinition getBeanDefinition(Class<?> beanClass);

}
