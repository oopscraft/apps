package org.oopscraft.apps.batch.item.file.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Length {

    int value() default 0;

    Align align() default Align.LEFT;

    char padChar() default ' ';

}
