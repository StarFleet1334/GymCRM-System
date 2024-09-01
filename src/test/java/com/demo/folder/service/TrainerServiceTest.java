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

  @Test
  public void testCreateTrainer() {
    Trainer trainer = new Trainer();
    trainer.setFirstName("Ilia");
    trainer.setLastName("Lataria");
    trainer.setSpecialization("Boxer");
    trainer.setActive(true);
    systemFacade.getTrainerService().createTrainer(trainer);
    // Let's verify that this trainer exists
    assertEquals("Ilia", trainer.getFirstName(), "Expected first name is Ilia");
    assertEquals("Lataria", trainer.getLastName(), "Expected last name is Lataria");
    assertEquals("Boxer", trainer.getSpecialization(), "Expected specialization is Boxer");
    assertTrue(trainer.isActive(), "Expected active is true");
  }


  @Test
  public void testUpdateTrainer() {
    Trainer trainer = new Trainer();
    trainer.setFirstName("Dondo");
    trainer.setSpecialization("Dancer");
    systemFacade.getTrainerService().update(4L, trainer);
    systemFacade.getTrainerService().getTrainer(4L).describe();
    // Check that name is changed
    assertEquals("Dondo", systemFacade.getTrainerService().getTrainer(4L).getFirstName(), "Expected first name is Dondo");
    // Check that Specialization is also changed
    assertEquals("Dancer",systemFacade.getTrainerService().getTrainer(4L).getSpecialization(), "Expected specialization to be Dancer");
    // Check userName
    assertEquals("Dondo.James",systemFacade.getTrainerService().getTrainer(4L).getUsername(), "Expected userName to be Dondo.James");
  }
}