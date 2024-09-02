package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.system.SystemFacade;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Using Context
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
class TrainingServiceTest {

  @Autowired
  private SystemFacade systemFacade;


  @Test
  public void testGetTraining_checkTrainingNameAndType() {
    Training training = systemFacade.getTrainingService().getTraining(3L);
    assertEquals("Cyber Security Fundamentals", training.getTrainingName(),
        "Expected training name is Cyber Security Fundamentals");
    assertEquals("HYBRID", training.getTrainingType().getTrainingTypeName().toUpperCase(),
        "Expected training type is HYBRID");
  }

  @Test
  public void testCreateTraining() {
    Training training = new Training();
    training.setTrainingName("Boxing");
    training.setTrainingDuration(120);

    Calendar calendar = Calendar.getInstance();

    calendar.set(Calendar.YEAR, 2024);
    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 7);
    calendar.set(Calendar.HOUR_OF_DAY, 7);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Date date = calendar.getTime();
    training.setTrainingDate(date);
    systemFacade.getTrainingService().createTraining(training);

    // check if training exists
    assertNotNull(systemFacade.getTrainingService().getTraining(training.getTrainingId()));

  }
}