package com.demo.folder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.system.SystemFacade;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestThree {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestThree.class);
  @Autowired
  private SystemFacade systemFacade;


  @Test
  public void checkReadAbilities() {
    // Check Trainee by id = 4's firstName and LastName
    assertEquals("Bob",systemFacade.getTraineeService().getTrainee(3L).getFirstName());
    assertEquals("Brown",systemFacade.getTraineeService().getTrainee(3L).getLastName());

    // Check Trainer by id = 3's firstName and LastName
    assertEquals("Sarah",systemFacade.getTrainerService().getTrainer(2L).getFirstName());
    assertEquals("Connor",systemFacade.getTrainerService().getTrainer(2L).getLastName());

    // Check Training by id = 4' trainingName and trainingType
    assertEquals("Cyber Security Fundamentals",systemFacade.getTrainingService().getTraining(3L).getTrainingName());
    assertEquals("HYBRID",systemFacade.getTrainingService().getTraining(3L).getTrainingType().getTrainingTypeName().toUpperCase());
  }

  @Test
  public void checkPasswordUniquenessTrainers() {
    List<Trainer> trainerList = systemFacade.getTrainerService().getAllTrainers();
    boolean check = true;
    if (trainerList.size() > 1) {
      Trainer trainer = trainerList.getFirst();
      for (int i = 1; i < trainerList.size(); i++) {
        if (trainer.getPassword().equals(trainerList.get(i).getPassword())) {
          check = false;
          break;
        }
      }
    }
    assertTrue(check);
  }

  @Test
  public void checkPasswordUniquenessTrainees() {
    List<Trainee> traineeList = systemFacade.getTraineeService().getAllTrainees();
    boolean check = true;
    if (traineeList.size() > 1) {
      Trainee trainee = traineeList.getFirst();
      for (int i = 1; i < traineeList.size(); i++) {
        if (trainee.getPassword().equals(traineeList.get(i).getPassword())) {
          check = false;
          break;
        }
      }
    }
    assertTrue(check);
  }

}
