package com.demo.folder.config.profiles;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

@Configuration
@Profile("dev")
public class DevConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(DevConfig.class);


  @PostConstruct
  public void postConstruct() {
    LOGGER.info("Development Configuration is active");
  }
}
