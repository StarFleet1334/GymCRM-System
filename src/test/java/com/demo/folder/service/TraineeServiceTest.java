package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.folder.model.Trainee;
import com.demo.folder.system.SystemFacade;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
    assertNull(systemFacade.getTraineeService().getTrainee(3L),
        "Deleted trainee should not be retrievable");
  }

  @Test
  public void testGetAllTrainees_passwordsAreUnique() {
    List<Trainee> traineeList = systemFacade.getTraineeService().getAllTrainees();
    Set<String> passwords = new HashSet<>();
    boolean allUnique = traineeList.stream()
        .allMatch(trainee -> passwords.add(trainee.getPassword()));
    assertEquals(true, allUnique, "Passwords should be unique across trainees");
  }

  @Test
  public void testCreateTrainee() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("A");
    trainee.setLastName("B");
    trainee.setAddress("St.OakLand");
    systemFacade.getTraineeService().createTrainee(trainee);
    // Let's check if that trainee got created
    AtomicBoolean exists = new AtomicBoolean(false);
    systemFacade.getTraineeService().getAllTrainees().forEach(s -> {
        if (s.getUsername().equals("A.B")) {
          exists.set(true);
      }
    });
    assertTrue(exists.get());
  }

  @Test
  public void testUpdateTrainee() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("A");
    systemFacade.getTraineeService().update(3L, trainee);
    systemFacade.getTraineeService().getTrainee(3L).describe();
    // Let's check if FirstName got updated
    assertEquals("A", systemFacade.getTraineeService().getTrainee(3L).getFirstName(), "Expected first name is A");
    // Let's check if the userName got updated
    assertEquals("A.Brown",systemFacade.getTraineeService().getTrainee(3L).getUsername(), "Expected username is A.Brown");
  }

  @Test
  public void testSelectTrainee() {
    Trainee trainee = systemFacade.getTraineeService().getTrainee(4L);
    assertEquals("Alice", trainee.getFirstName(), "Expected first name is Alice");
    assertEquals("Johnson", trainee.getLastName(), "Expected last name is Johnson");
  }
}