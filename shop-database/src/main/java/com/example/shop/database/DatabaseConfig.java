package com.example.shop.database;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@SpringBootConfiguration
@SpringBootTest(classes=DatabaseConfig.class)
@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:create-db.sql")
                .addScript("classpath:insert-db.sql")
                .build();
    }


    @Bean(name="myNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate myNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name="myJdbcTemplate")
    public JdbcTemplate myJdbcTemplate(DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }


}
