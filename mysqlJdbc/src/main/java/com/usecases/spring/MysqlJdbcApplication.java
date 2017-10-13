package com.usecases.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackageClasses = {MysqlJdbcApplication.class, Commons.class})
public class MysqlJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlJdbcApplication.class, args);
	}
}