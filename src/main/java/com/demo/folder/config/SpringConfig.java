package com.demo.folder.config;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.dao.TrainerDAO;
import com.demo.folder.dao.TrainingDAO;
import com.demo.folder.daoImpl.TraineeDAOImpl;
import com.demo.folder.daoImpl.TrainerDAOImpl;
import com.demo.folder.daoImpl.TrainingDAOImpl;
import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingService;
import com.demo.folder.storage.StorageBean;
import com.demo.folder.system.SystemFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.demo.folder")
@PropertySource("classpath:application.properties")
public class SpringConfig {

  @Bean
  public SystemFacade systemFacade(TraineeService traineeService, TrainerService trainerService,
      TrainingService trainingService, StorageBean storageBean) {
    return new SystemFacade(traineeService, trainerService, trainingService, storageBean);
  }

  @Bean
  public StorageBean storageBean() {
    return new StorageBean();
  }

  @Bean
  public TraineeDAO traineeDAO(StorageBean storageBean) {
    return new TraineeDAOImpl(storageBean);
  }

  @Bean
  public TrainerDAO trainerDAO(StorageBean storageBean) {
    return new TrainerDAOImpl(storageBean);
  }

  @Bean
  public TrainingDAO trainingDAO(StorageBean storageBean) {
    return new TrainingDAOImpl(storageBean);
  }

  @Bean
  public TrainingService trainingService(TrainingDAO trainingDAO) {
    TrainingService trainingService = new TrainingService();
    trainingService.setTrainingDAO(trainingDAO);
    return trainingService;
  }

  @Bean
  public TrainerService trainerService(TrainerDAO trainerDAO) {
    TrainerService trainerService = new TrainerService();
    trainerService.setTrainerDAO(trainerDAO);
    return trainerService;
  }

  @Bean
  public TraineeService traineeService(TraineeDAO traineeDAO) {
    TraineeService traineeService = new TraineeService();
    traineeService.setTraineeDAO(traineeDAO);
    return traineeService;
  }
}
