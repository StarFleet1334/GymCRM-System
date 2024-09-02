package com.demo.folder.config;

import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.demo.folder")
@PropertySource("classpath:application.properties")
public class SpringConfig {

  @Bean
  public TrainingService trainingService() {
    return new TrainingService();
  }

  @Bean
  public TrainerService trainerService() {
    return new TrainerService();
  }

  @Bean
  public TraineeService traineeService() {
    return new TraineeService();
  }
}
