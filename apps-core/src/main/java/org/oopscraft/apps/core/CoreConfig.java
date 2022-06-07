package org.oopscraft.apps.core;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "core")
@RequiredArgsConstructor
public class CoreConfig {

    private final Map<String, HikariConfig> datasource;

}
