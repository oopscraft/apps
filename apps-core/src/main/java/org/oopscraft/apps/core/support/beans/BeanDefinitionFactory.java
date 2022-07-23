package org.oopscraft.apps.core.support.beans;

import org.oopscraft.apps.core.support.beans.factory.ComponentDefinitionFactory;
import org.oopscraft.apps.core.support.beans.factory.ControllerDefinitionFactory;
import org.oopscraft.apps.core.support.beans.factory.MapperDefinitionFactory;
import org.oopscraft.apps.core.support.beans.factory.RepositoryDefinitionFactory;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.HashSet;
import java.util.Set;

public abstract class BeanDefinitionFactory {

    /**
     * factory registry
     */
    private static Set<BeanDefinitionFactory> beaDefinitionFactoryRegistry = new HashSet<BeanDefinitionFactory>(){{
        add(new ComponentDefinitionFactory());
        add(new MapperDefinitionFactory());
        add(new RepositoryDefinitionFactory());
        add(new ControllerDefinitionFactory());
    }};

    /**
     * gets bean definition factory
     * @param beanClass
     * @return
     * @throws UnsupportedBeanTypeException
     */
    public static BeanDefinitionFactory getBeanDefinitionFactory(Class<?> beanClass) throws UnsupportedBeanTypeException {
        for(BeanDefinitionFactory beanDefinitionFactory : beaDefinitionFactoryRegistry){
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
