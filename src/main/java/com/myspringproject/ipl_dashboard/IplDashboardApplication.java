package com.myspringproject.ipl_dashboard;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class IplDashboardApplication {

	private static final Logger log = LoggerFactory.getLogger(IplDashboardApplication.class);

	@Autowired
	private DataSource dataSource; // Spring will autowire the DataSource bean (HikariCP)

	@Autowired
	private Environment env; // To access application properties

	public static void main(String[] args) {
		SpringApplication.run(IplDashboardApplication.class, args);
	}

	@PostConstruct
	public void printDataSourceUrl() {
		log.info("Application starting up...");
		if (dataSource instanceof HikariDataSource) {
			HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
			log.info("API/JPA Data Source URL (from HikariCP): {}", hikariDataSource.getJdbcUrl());
		} else {
			// Fallback for other DataSource types, though HikariCP is expected
			log.info("API/JPA Data Source URL (from Environment fallback): {}",
					env.getProperty("spring.datasource.url"));
			log.warn("DataSource is not a HikariDataSource. Actual type: {}", dataSource.getClass().getName());
		}
		log.info("--- End of DataSource URL check ---");
	}

}
