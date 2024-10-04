package com.demo.folder.config.profiles;


import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Profile("stg")
public class StgConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(StgConfig.class);

  @PostConstruct
  public void postConstruct() {
    LOGGER.info("Production Configuration is active");
  }

  @Bean
  public DataSource dataSource() {
    LOGGER.info("Setting up DataSource for STAGING environment");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:stgdb;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");

    LOGGER.info("DB URL: {}", dataSource.getUrl());
    LOGGER.info("DB Username: {}", dataSource.getUsername());

    return dataSource;
  }

}
