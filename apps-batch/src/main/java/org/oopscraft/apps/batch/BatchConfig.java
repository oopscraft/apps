package org.oopscraft.apps.batch;


import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;

public class BatchConfig {

    @Getter
    private HikariConfig datasource;

}
