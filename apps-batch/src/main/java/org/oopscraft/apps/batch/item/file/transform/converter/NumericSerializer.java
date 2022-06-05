package org.oopscraft.apps.batch.item.file.transform.converter;

import com.google.common.collect.ImmutableSet;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.math.BigDecimal;
import java.util.Set;

public class NumericSerializer implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair[] pairs = new ConvertiblePair[] {
                new ConvertiblePair(Integer.class, String.class),
                new ConvertiblePair(Long.class, String.class),
                new ConvertiblePair(Double.class, String.class),
                new ConvertiblePair(Float.class, String.class),
                new ConvertiblePair(Number.class, String.class),
                new ConvertiblePair(BigDecimal.class, String.class)
        };
        return ImmutableSet.copyOf(pairs);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if(source == null) {
            return "";
        }
        return String.valueOf(source);
    }
}
