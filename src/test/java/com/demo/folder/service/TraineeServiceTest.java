package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.User;
import java.sql.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@Transactional
public class TraineeServiceTest {


  @Autowired
  private TrainerService trainerService;

  @Autowired
  private TrainingTypeService trainingTypeService;

  @Autowired
  private UserService userService;
  @Autowired
  private TraineeService traineeService;

  @Test
  public void testCreateTrainee() {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setUsername("Jane.Smith");
    user.setPassword("ASddsA");
    user.setActive(true);

    Trainee trainee = new Trainee();
    trainee.setUser(user);

    traineeService.createTrainee(trainee);

    assertNotNull(traineeService.findTraineeByUsername("Jane.Smith"));
  }

  @Test
  public void testFieldCreation() {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setUsername("Jane.Smith");
    user.setPassword("ASddsA");
    user.setActive(true);

    Trainee trainee = new Trainee();
    trainee.setAddress("Tbilisi/Kazbegi Street");
    Date date = Date.valueOf("2002-08-22");
    trainee.setDateOfBirth(date);
    trainee.setUser(user);

    traineeService.createTrainee(trainee);

    // check if the address is correct
    assertEquals("Tbilisi/Kazbegi Street", traineeService.findTraineeByUsername("Jane.Smith").getAddress());
    // check if the Date is correct
    assertEquals(date, traineeService.findTraineeByUsername("Jane.Smith").getDateOfBirth());
  }


  @Test
  public void testActiveTrainee() {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setUsername("Jane.Smith");
    user.setPassword("ASddsA");
    user.setActive(false);

    Trainee trainee = new Trainee();
    trainee.setUser(user);

    traineeService.createTrainee(trainee);
    traineeService.activateTrainee(traineeService.findTraineeByUsername("Jane.Smith").getId());
    assertEquals(true,traineeService.findTraineeByUsername("Jane.Smith").getUser().isActive());
  }

  @Test
  public void testDeActivateTrainee() {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setUsername("Jane.Smith");
    user.setPassword("ASddsA");
    user.setActive(true);

    Trainee trainee = new Trainee();
    trainee.setUser(user);

    traineeService.createTrainee(trainee);
    traineeService.deactivateTrainee(traineeService.findTraineeByUsername("Jane.Smith").getId());
    assertEquals(false,traineeService.findTraineeByUsername("Jane.Smith").getUser().isActive());
  }




}
