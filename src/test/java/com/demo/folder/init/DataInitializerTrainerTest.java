package com.demo.folder.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.model.Trainer;
import com.demo.folder.system.SystemFacade;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
class DataInitializerTrainerTest {

  @Autowired
  private SystemFacade systemFacade;
  private static final String TRAINER_PATH = "src/main/resources/data/trainer.csv";


  @Test
  public void initialDataTrainerCheck() throws IOException {
    List<Trainer> trainers = systemFacade.getTrainerService().getAllTrainers();

    try (CSVReader csvReader = new CSVReader(
        new FileReader(TRAINER_PATH))) {
      String[] nextRecord;
      int index = 0;

      csvReader.readNext();

      while ((nextRecord = csvReader.readNext()) != null) {
        Long userId = Long.parseLong(nextRecord[0]);
        String firstName = nextRecord[1];
        String lastName = nextRecord[2];
        String username = nextRecord[3];
        String password = nextRecord[4];
        String specialization = nextRecord[5];
        boolean isActive = Boolean.parseBoolean(nextRecord[6]);

        assertTrainer(trainers.get(index), userId, firstName, lastName, username, password,
            specialization, isActive);
        index++;
      }
      assertEquals(index, trainers.size(), "Number of trainers should match the CSV records.");
    }
  }

  private void assertTrainer(Trainer trainer, Long userId, String firstName, String lastName,
      String username, String password, String specialization, boolean isActive) {
    assertEquals(userId, trainer.getUserId(), "User ID does not match.");
    assertEquals(firstName, trainer.getFirstName(), "First name does not match.");
    assertEquals(lastName, trainer.getLastName(), "Last name does not match.");
    assertEquals(username, trainer.getUsername(), "Username does not match.");
    assertEquals(password, trainer.getPassword(), "Password does not match.");
    assertEquals(specialization, trainer.getSpecialization(), "Specialization does not match.");
    assertEquals(isActive, trainer.isActive(), "Active status does not match.");
  }
}