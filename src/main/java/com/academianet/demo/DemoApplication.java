package com.academianet.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "com.academianet.demo.repository",
        excludeFilters = @Filter(
                type = FilterType.REGEX,
                pattern = "com\\.academianet\\.demo\\.repository\\.mongo\\..*"))
@EnableMongoRepositories(basePackages = "com.academianet.demo.repository.mongo")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
