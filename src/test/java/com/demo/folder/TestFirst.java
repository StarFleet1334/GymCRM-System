package com.demo.folder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.system.SystemFacade;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFirst {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestFirst.class);
  @Autowired
  private SystemFacade systemFacade;

  @Test
  void identicalTrainerWithFirstAndLastName() {
    Trainer first_trainer = new Trainer();
    first_trainer.setFirstName("a");
    first_trainer.setLastName("b");
    systemFacade.getTrainerService().createTrainer(first_trainer);
    assertEquals("a.b",first_trainer.getUsername(),"UserName should be a.b");
    Trainer second_trainer = new Trainer();
    second_trainer.setFirstName("a");
    second_trainer.setLastName("b");
    systemFacade.getTrainerService().createTrainer(second_trainer);
    assertEquals("a.b1",second_trainer.getUsername(),"UserName should be a.b1");
    systemFacade.getTrainerService().getAllTrainers().forEach(Trainer::describe);
    Trainer third_trainer = new Trainer();
    third_trainer.setFirstName("a");
    third_trainer.setLastName("b");
    systemFacade.getTrainerService().createTrainer(third_trainer);
    assertEquals("a.b2",third_trainer.getUsername(),"UserName should be a.b2");
    systemFacade.getTrainerService().getAllTrainers().forEach(Trainer::describe);
  }

  @Test
  void validUserNameCheck() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("John@#$");
    trainee.setLastName("Doe 123");
    systemFacade.getTraineeService().createTrainee(trainee);
    assertEquals("John@#$.Doe 123",trainee.getUsername(),"UserName should be john.doe123");
  }

  // this should fail
  @Test
  void nullFirstAndLastNameCheck() {
    Trainee trainee = new Trainee();
    trainee.setFirstName(null);
    trainee.setLastName(null);
    systemFacade.getTraineeService().createTrainee(trainee);
    assertNull(trainee.getFirstName(), "UserName should be null");
    assertNull(trainee.getLastName(), "LastName should be null");
    assertEquals("Unknown.Unknown",trainee.getUsername(),"UserName should be Unknown.Unknown");
  }

  @Test
  void updateTrainerInformation() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("Jane");
    trainee.setLastName("Smith");

    // re-updating firstName and lastName
    trainee.setFirstName("Janet");
    trainee.setLastName("Doe");
    assertEquals("Janet",trainee.getFirstName(),"UserName should be Janet");
    assertEquals("Doe",trainee.getLastName(),"UserName should be Doe");
    assertEquals("Janet.Doe",trainee.getUsername(),"UserName should be Janet.Doe");
  }

  @Test
  void deletingTrainee() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("AA");
    trainee.setLastName("BB");
    systemFacade.getTraineeService().createTrainee(trainee);
    int sizeBefore = systemFacade.getTraineeService().getAllTrainees().size();
    systemFacade.getTraineeService().deleteTrainee(trainee.getUserId());
    assertNull(systemFacade.getTraineeService().getTrainee(trainee.getUserId()));
    assertEquals(sizeBefore-1,systemFacade.getTraineeService().getAllTrainees().size());
  }




}
