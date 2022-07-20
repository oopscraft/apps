package org.oopscraft.apps.core.bean.definition;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

@Slf4j
public class MapperBeanDefinitionFactory extends AbstractBeanDefinitionFactory {

    private String sqlSessionFactoryRef = "sqlSessionFactory";

    @Override
    public boolean support(Class<?> beanClass) {
        if(beanClass.isAnnotation()){
            return false;
        }
        if (beanClass.isInterface() && beanClass.isAnnotationPresent(Mapper.class)) {
            return true;
        }
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> mapperClass) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class);
        beanDefinitionBuilder.addConstructorArgValue(mapperClass);
        beanDefinitionBuilder.addPropertyReference("sqlSessionFactory", sqlSessionFactoryRef);
        return beanDefinitionBuilder.getBeanDefinition();
    }

}
