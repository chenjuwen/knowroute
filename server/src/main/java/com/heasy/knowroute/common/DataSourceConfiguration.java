package com.heasy.knowroute.common;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

//开启注解式事务
@EnableTransactionManagement

@Configuration
public class DataSourceConfiguration implements TransactionManagementConfigurer{
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.knowroute")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
    public JdbcTemplate sqliteJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	
	/**
	 * 实现接口 TransactionManagementConfigurer 方法，其返回值代表在拥有多个事务管理器的情况下默认使用的事务管理器
	 */
	@Bean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
}
