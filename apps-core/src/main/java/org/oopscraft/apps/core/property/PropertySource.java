package org.oopscraft.apps.core.property;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PropertySource extends org.springframework.core.env.PropertySource<PropertyService> {

    private final PropertyService propertyService;

    private final Environment environment;

    /**
     * PropertySource
     * @param propertyService
     * @param environment
     */
    public PropertySource(PropertyService propertyService, Environment environment) {
        super(PropertySource.class.getName(), propertyService);
        this.propertyService = propertyService;
        this.environment = environment;
    }

    /**
     * getProperty
     * @param id
     * @return
     */
    @Override
    public String getProperty(String id) {
        Property property = propertyService.getProperty(id);
        if(property != null) {
            return property.getValue();
        }else{
            return environment.getProperty(id);
        }
    }
}
