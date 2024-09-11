package com.demo.folder;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.service.ProfileService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

    // Get the ProfileService bean from the Spring context
    ProfileService profileService = context.getBean(ProfileService.class);

    // Start the profile service
    profileService.start();
  }
}
