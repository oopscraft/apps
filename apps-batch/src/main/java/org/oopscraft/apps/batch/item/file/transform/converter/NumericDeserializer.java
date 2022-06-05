package org.oopscraft.apps.batch.item.file.transform.converter;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
public class NumericDeserializer implements GenericConverter{

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair[] pairs = new ConvertiblePair[] {
                new ConvertiblePair(String.class, Integer.class),
                new ConvertiblePair(String.class, Long.class),
                new ConvertiblePair(String.class, Double.class),
                new ConvertiblePair(String.class, Float.class),
                new ConvertiblePair(String.class, Number.class),
                new ConvertiblePair(String.class, BigDecimal.class)
        };
        return ImmutableSet.copyOf(pairs);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if(source == null || StringUtils.isEmpty(source.toString())) {
            if(targetType.getType().isPrimitive()){
                return 0;
            }else{
                return null;
            }
        }

        Class<?> targetTypeClass = targetType.getType();
        try {
            if (targetTypeClass == Integer.class || targetTypeClass == int.class) {
                return Integer.valueOf(source.toString());
            }
            if (targetTypeClass == Long.class || targetTypeClass == long.class) {
                return Long.valueOf(source.toString());
            }
            if (targetTypeClass == Double.class || targetTypeClass == double.class) {
                return Double.valueOf(source.toString());
            }
            if (targetTypeClass == Float.class || targetTypeClass == float.class) {
                return Float.valueOf(source.toString());
            }
            if (targetTypeClass == Number.class) {
                return Integer.valueOf(source.toString());
            }
            if (targetTypeClass == BigDecimal.class) {
                return new BigDecimal(source.toString());
            }
        }catch(Exception e){
            log.warn("Invalid value[{}]({}):{}", source, targetTypeClass, e.getMessage());
            return null;
        }
        throw new RuntimeException("incompatible type");
    }

}
