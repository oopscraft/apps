package org.oopscraft.apps.core.data;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoutingDataSourceKey {

    String value() default "";

}
