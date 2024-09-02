package com.demo.folder;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// Main Class
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        SpringConfig.class);

    TrainingService trainingService = context.getBean(TrainingService.class);
    TraineeService traineeService = context.getBean(TraineeService.class);
    TrainerService trainerService = context.getBean(TrainerService.class);

    LOGGER.info("Trainees------------------------------------------------");
    traineeService.getAllTrainees().forEach(Trainee::describe);
    LOGGER.info("Trainers-----------------------------------------------");
    trainerService.getAllTrainers().forEach(Trainer::describe);
    LOGGER.info("Trainings------------------------------------------------");
    trainingService.getAllTrainings().forEach(Training::describe);
  }
}
