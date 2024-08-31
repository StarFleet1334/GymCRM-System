package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.demo.folder.model.Trainee;
import com.demo.folder.system.SystemFacade;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TraineeServiceTest {

  @Autowired
  private SystemFacade systemFacade;

  @Test
  public void testGetTrainee_checkFirstNameAndLastName() {
    Trainee trainee = systemFacade.getTraineeService().getTrainee(3L);
    assertEquals("Bob", trainee.getFirstName(), "Expected first name is Bob");
    assertEquals("Brown", trainee.getLastName(), "Expected last name is Brown");
  }

  @Test
  public void testDeleteTrainee_decreasesTraineeCountAndTraineeNotFound() {
    int beforeSize = systemFacade.getTraineeService().getAllTrainees().size();
    systemFacade.getTraineeService().deleteTrainee(3L);
    int afterSize = systemFacade.getTraineeService().getAllTrainees().size();

    assertEquals(beforeSize - 1, afterSize, "Trainee count should decrease by 1 after deletion");
    assertNull(systemFacade.getTraineeService().getTrainee(3L), "Deleted trainee should not be retrievable");
  }

  @Test
  public void testGetAllTrainees_passwordsAreUnique() {
    List<Trainee> traineeList = systemFacade.getTraineeService().getAllTrainees();
    Set<String> passwords = new HashSet<>();
    boolean allUnique = traineeList.stream().allMatch(trainee -> passwords.add(trainee.getPassword()));
    assertEquals(true, allUnique, "Passwords should be unique across trainees");
  }
}