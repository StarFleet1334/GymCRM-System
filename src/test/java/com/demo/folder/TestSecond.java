package com.demo.folder;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.system.SystemFacade;
import java.util.Calendar;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSecond {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestFirst.class);
  @Autowired
  private SystemFacade systemFacade;

  @Test
  public void multipleTraineeToTrainingCheck() {
    int size = systemFacade.getTrainingService().getAllTrainings().size();
    Calendar futureDate = Calendar.getInstance();
    futureDate.add(Calendar.DATE, 1);

    Training training = new Training();
    training.setTrainingName("Boxing");
    training.setTrainingDate(futureDate.getTime());

    Trainer trainer1 = new Trainer();
    trainer1.setFirstName("saba");
    trainer1.setLastName("Dumbadze");
    training.setTrainerId(trainer1.getUserId());

    Training training1 = new Training();
    training1.setTrainingName("Boxing");

    Trainer trainer2 = new Trainer();
    trainer2.setFirstName("Ilia");
    trainer2.setLastName("Lataria");
    training1.setTrainerId(trainer2.getUserId());

    training1.setTrainingDate(futureDate.getTime());

    systemFacade.getTrainerService().createTrainer(trainer1);
    systemFacade.getTrainerService().createTrainer(trainer2);
    systemFacade.getTrainingService().createTraining(training);
    systemFacade.getTrainingService().createTraining(training1);

    systemFacade.getTrainerService().getAllTrainers().forEach(Trainer::describe);
    systemFacade.getTrainingService().getAllTrainings().forEach(Training::describe);
    assertEquals(6,systemFacade.getTrainingService().getTraining((long) (size+1)).getTrainerId());
    assertEquals(7,systemFacade.getTrainingService().getTraining((long) (size+2)).getTrainerId());

  }
}
