package org.oopscraft.apps.core.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {
	
	@Override
	public String convertToDatabaseColumn(String attribute) {
		return "<ENC>".concat(attribute).concat("</ENC>");

	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		return dbData.replace("<ENC>","").replace("</ENC>","");
	}

}