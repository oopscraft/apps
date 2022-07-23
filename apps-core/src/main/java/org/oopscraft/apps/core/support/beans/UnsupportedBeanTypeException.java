package org.oopscraft.apps.core.support.beans;

public class UnsupportedBeanTypeException extends Exception {

    /**
     * constructor
     * @param beanClass
     */
    public UnsupportedBeanTypeException(Class<?> beanClass) {
        super(String.format("[%s] is unsupported bean type", beanClass.getName()));
    }
}
