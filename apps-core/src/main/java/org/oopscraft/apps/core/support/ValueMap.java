package org.oopscraft.apps.core.support;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.LinkedHashMap;

public class ValueMap extends LinkedHashMap<String,Object> {

	private static final long serialVersionUID = -8060385408388947013L;

	public ValueMap(){
		super();
	}
	
	@Override
	public Object put(String name, Object value) {
		// CLOB case
		if(value instanceof Clob) {
			StringBuffer buffer = new StringBuffer();
			Clob clob = (Clob)value;
			Reader reader = null;
			try {
				reader = clob.getCharacterStream();
				char[] buff = new char[1024];
				int nchars = 0;
				while ((nchars = reader.read(buff)) > 0) {
					buffer.append(buff, 0, nchars);
				}
			}catch(Exception e) {
				buffer.append(e.getMessage());
			}finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						buffer.append(e.getMessage());
					}
				}
			}
			return super.put(name, buffer.toString());
		}
		// default
		return super.put(name, value);
	}

	/**
	 * Setter method
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value) {
		this.put(name, value);
	}
	
	/**
	 * Setter string
	 * @param name
	 * @param value
	 */
	public void setString(String name, Object value) {
		if(value instanceof String) {
			this.set(name, value);
		}else{
			try {
				this.set(name, value.toString());
			}catch(Exception e){
				this.set(name, "");
			}
		}
	}

	/**
	 * Getter string
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		Object value = this.get(name);
		if(value instanceof String) {
			return (String)value;
		}else{
			try {
				return value.toString();
			}catch(Exception e){
				return "";
			}
		}
	}
	
	/**
	 * Setter number
	 * @param name
	 * @param value
	 */
	public void setNumber(String name, Object value) {
		if(value instanceof BigDecimal) {
			this.set(name, value);
		}else{
			try {
				this.set(name, new BigDecimal(value.toString()));
			}catch(Exception e){
				this.set(name, BigDecimal.ZERO);
			}
		}
	}
	
	public BigDecimal getNumber(String name) {
		Object value = this.get(name);
		if(value instanceof BigDecimal) {
			return (BigDecimal)value;
		}else{
			try {
				return new BigDecimal(value.toString());
			}catch(Exception e){
				return BigDecimal.ZERO;
			}
		}
	}
	
}