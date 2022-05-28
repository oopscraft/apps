package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariConfig;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "batch")
@ConstructorBinding
@RequiredArgsConstructor
public class BatchConfig {

    @Getter
    private final boolean enableDatasource;

    @Getter
    private final HikariConfig datasource;

}
