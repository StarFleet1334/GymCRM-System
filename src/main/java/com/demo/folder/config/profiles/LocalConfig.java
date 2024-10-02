package com.demo.folder.config.profiles;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfig.class);


  @PostConstruct
  public void postConstruct() {
    LOGGER.info("Local Configuration is active");
  }
}
