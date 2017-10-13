package com.usecases.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Profile({"test"})
    @Bean(name = "spring.datasource", destroyMethod = "shutdown")
    public DataSource dataSourceDevTest() {
        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .continueOnError(false)
                .setName("person_test")
                .addScript("classpath:schema-test.sql")
                .addScript("classpath:data-test.sql").build();
        return embeddedDatabase;
    }
}
