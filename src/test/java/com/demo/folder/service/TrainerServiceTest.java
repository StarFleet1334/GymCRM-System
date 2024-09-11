package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.demo.folder.config.SpringConfig;
import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.TrainingType;
import com.demo.folder.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@Transactional
public class TrainerServiceTest {

  @Autowired
  private TrainerService trainerService;

  @Autowired
  private TrainingTypeService trainingTypeService;

  @Autowired
  private UserService userService;

  @Test
  @Rollback
  public void testCreateTrainer() {
    User user = new User();
    user.setFirstName("Jane");
    user.setLastName("Smith");
    user.setUsername("janesmith");
    user.setPassword("password");
    user.setActive(true);

    TrainingType specialization = new TrainingType();
    specialization.setTrainingTypeName("Strength");
    trainingTypeService.createTrainingType(specialization);

    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(specialization);

    Trainer savedTrainer = trainerService.createTrainer(trainer);

    assertEquals("Jane", savedTrainer.getUser().getFirstName());
  }




}
