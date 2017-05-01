package com.usecases.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {MysqlJpaApplication.class, Commons.class})
public class MysqlJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlJpaApplication.class, args);
	}
}
