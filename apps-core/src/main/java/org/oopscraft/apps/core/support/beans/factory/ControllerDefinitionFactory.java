package org.oopscraft.apps.core.support.beans.factory;

import org.oopscraft.apps.core.support.beans.BeanDefinitionFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

public class ControllerDefinitionFactory extends BeanDefinitionFactory {

    @Override
    public boolean support(Class<?> beanClass) {
        if(beanClass.isAnnotation()){
            return false;
        }
        if(beanClass.isAnnotationPresent(Controller.class)
        || beanClass.isAnnotationPresent(RestController.class)){
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
