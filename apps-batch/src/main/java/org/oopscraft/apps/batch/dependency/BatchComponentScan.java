package org.oopscraft.apps.batch.dependency;

import java.lang.annotation.*;

/**
 * BatchComponentScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BatchComponentScan {

    String[] value() default {};

}