package com.demo.folder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.folder.model.Trainer;
import com.demo.folder.system.SystemFacade;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TrainerServiceTest {

  @Autowired
  private SystemFacade systemFacade;

  @Test
  public void testGetTrainer_checkFirstNameAndLastName() {
    Trainer trainer = systemFacade.getTrainerService().getTrainer(2L);
    assertEquals("Sarah", trainer.getFirstName(), "Expected first name is Sarah");
    assertEquals("Connor", trainer.getLastName(), "Expected last name is Connor");
  }

  @Test
  public void testGetAllTrainers_passwordsAreUnique() {
    List<Trainer> trainerList = systemFacade.getTrainerService().getAllTrainers();
    Set<String> passwords = new HashSet<>();
    boolean allUnique = trainerList.stream()
        .allMatch(trainer -> passwords.add(trainer.getPassword()));
    assertTrue(allUnique, "Passwords should be unique across trainers");
  }
}