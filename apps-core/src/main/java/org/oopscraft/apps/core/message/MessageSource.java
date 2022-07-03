package org.oopscraft.apps.core.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class MessageSource extends ReloadableResourceBundleMessageSource {

	private static final String PROPERTIES_SUFFIX = ".properties";

	private final MessageService messageService;

	private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	/**
	 * refreshProperties
	 * @param filename
	 * @param propHolder
	 * @return
	 */
	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			return refreshClassPathProperties(filename, propHolder);
		} else {
			return super.refreshProperties(filename, propHolder);
		}
	}

	/**
	 * refreshClassPathProperties
	 * @param filename
	 * @param propHolder
	 * @return
	 */
	private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
		Properties properties = new Properties();
		long lastModified = -1;
		try {
			Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
			for (Resource resource : resources) {
				String sourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
				PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
				properties.putAll(holder.getProperties());
				if (lastModified < resource.lastModified())
					lastModified = resource.lastModified();
			}
		} catch (IOException ignored) {
		}
		return new PropertiesHolder(properties, lastModified);
	}

	/**
	 * Resolves the given message code as key in the retrieved bundle files,
	 * returning the value found in the bundle as-is (without MessageFormat parsing).
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		String result = super.resolveCodeWithoutArguments(code, locale);
		if(result == null) {
			try {
				Message message = messageService.getMessage(code);
				String value = message.getValue();
				if(value != null) {
					result = value;
				}
			}catch(Exception ignore) {
				log.warn(ignore.getMessage());
			}
		}
		return result;
	}

	/**
	 * Resolves the given message code as key in the retrieved bundle files,
	 * using a cached MessageFormat instance per message code.
	 */
	@Override
	@Nullable
	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat messageFormat = super.resolveCode(code, locale);
		if(messageFormat == null) {
			try {
				Message message = messageService.getMessage(code);
				String value = message.getValue();
				if(value != null) {
					messageFormat = new MessageFormat(value, locale);
				}
			}catch(Exception ignore) {
				log.warn(ignore.getMessage());	
			}
		}
		return messageFormat;
	}


}
