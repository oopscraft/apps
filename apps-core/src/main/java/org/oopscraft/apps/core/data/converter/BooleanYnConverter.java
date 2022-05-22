package org.oopscraft.apps.core.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanYnConverter implements AttributeConverter<Boolean, String> {

	private static final String YES = "Y";
	private static final String NO = "N";

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		if(attribute != null && attribute == true){
			return YES;
		}else{
			return NO;
		}
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData) {
		return YES.equals(dbData) ? true : false;
	}

}