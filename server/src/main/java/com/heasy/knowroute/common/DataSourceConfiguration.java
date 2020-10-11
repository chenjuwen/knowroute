package com.heasy.knowroute.common;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfiguration {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.lottery")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
    public JdbcTemplate sqliteJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
