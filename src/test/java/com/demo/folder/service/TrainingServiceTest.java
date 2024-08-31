package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.model.Training;
import com.demo.folder.system.SystemFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
}