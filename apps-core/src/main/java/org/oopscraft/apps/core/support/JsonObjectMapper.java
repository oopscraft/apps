package org.oopscraft.apps.core.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

@Slf4j
public class JsonObjectMapper {

	public static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
	}

	/**
	 * return objectMapper
	 * 
	 * @return
	 */
	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * toJson
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object obj) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		}catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * toJson
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static String toJson(List<?> list) {
		try {
			return objectMapper.writeValueAsString(list);
		}catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * toObject
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		}catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * toList
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(Collection.class, clazz));
		}catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * convert JSON string to string
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public static String stringify(String string) {
		try {
			String json = objectMapper.writeValueAsString(string);
			return objectMapper.writeValueAsString(json);
		}catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * toJson
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isJson(String value) {
		boolean valid = true;
		try {
			objectMapper.readTree(value);
		} catch (Exception e) {
			log.debug(e.getMessage());
			valid = false;
		}
		return valid;
	}

}