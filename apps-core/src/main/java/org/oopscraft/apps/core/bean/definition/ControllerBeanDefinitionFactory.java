package org.oopscraft.apps.core.bean.definition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

public class ControllerBeanDefinitionFactory extends AbstractBeanDefinitionFactory {

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
