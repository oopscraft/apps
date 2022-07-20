package org.oopscraft.apps.core.bean.definition;

import org.oopscraft.apps.core.bean.UnsupportedBeanTypeException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBeanDefinitionFactory {

    private static Set<AbstractBeanDefinitionFactory> beaDefinitionGeneratorRegistry = new HashSet<AbstractBeanDefinitionFactory>(){{
        add(new ComponentBeanDefinitionFactory());
        add(new MapperBeanDefinitionFactory());
        add(new RepositoryDefinitionFactory());
        add(new ControllerBeanDefinitionFactory());
    }};

    public static AbstractBeanDefinitionFactory getBeanDefinitionFactory(Class<?> beanClass) throws UnsupportedBeanTypeException {
        for(AbstractBeanDefinitionFactory beanDefinitionFactory : beaDefinitionGeneratorRegistry){
            if(beanDefinitionFactory.support(beanClass)){
                return beanDefinitionFactory;
            }
        }
        return null;
    }

    public abstract boolean support(Class<?> beanClass);

    public abstract BeanDefinition getBeanDefinition(Class<?> beanClass);

}
