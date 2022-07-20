package org.oopscraft.apps.core.bean.definition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public class ComponentBeanDefinitionFactory extends AbstractBeanDefinitionFactory {

    @Override
    public boolean support(Class<?> beanClass) {
        if(beanClass.isAnnotation()){
            return false;
        }
        if(beanClass.isAnnotationPresent(Component.class)
        || beanClass.isAnnotationPresent(Service.class)
        ){
            return true;
        }
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> componentClass) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(componentClass);
        return beanDefinitionBuilder.getBeanDefinition();
    }

}
