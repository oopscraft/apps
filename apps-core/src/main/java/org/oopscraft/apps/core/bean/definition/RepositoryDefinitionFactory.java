package org.oopscraft.apps.core.bean.definition;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.stereotype.Repository;

public class RepositoryDefinitionFactory extends AbstractBeanDefinitionFactory {

    private String transactionManager = "transactionManager";

    private String entityManager = "entityManager";

    private String escapeCharacter = "escapeCharacter";

    private String mappingContext = "jpaMappingContext";

    private String entityManagerFactoryRef = "entityManagerFactory";

    @Override
    public boolean support(Class<?> beanClass) {
        if(beanClass.isAnnotation()){
            return false;
        }
        if(beanClass.isAnnotationPresent(Repository.class)){
            return true;
        }
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> repositoryClass) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactoryBean.class);
        beanDefinitionBuilder.addConstructorArgValue(repositoryClass);
        beanDefinitionBuilder.addPropertyValue("transactionManager", transactionManager);
        beanDefinitionBuilder.addPropertyValue("entityManager", getEntityManagerBeanDefinitionFor());
        beanDefinitionBuilder.addPropertyReference("mappingContext", mappingContext);
        return beanDefinitionBuilder.getBeanDefinition();
    }

    /**
     * getEntityManagerBeanDefinitionFor
     * @return
     */
    private AbstractBeanDefinition getEntityManagerBeanDefinitionFor() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition("org.springframework.orm.jpa.SharedEntityManagerCreator");
        builder.setFactoryMethod("createSharedEntityManager");
        builder.addConstructorArgReference(entityManagerFactoryRef);
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        return beanDefinition;
    }

}
