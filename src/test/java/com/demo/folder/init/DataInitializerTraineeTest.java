package com.demo.folder.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.model.Trainee;
import com.demo.folder.system.SystemFacade;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataInitializerTraineeTest {

  @Autowired
  private SystemFacade systemFacade;
  private static final String TRAINEE_PATH = "src/main/resources/data/trainee.csv";
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


  @Test
  public void initialDataTraineeCheck() throws ParseException {
    List<Trainee> trainees = systemFacade.getTraineeService().getAllTrainees();
    try (CSVReader csvReader = new CSVReader(new FileReader(TRAINEE_PATH))) {
      String[] nextRecord;
      int index = 0;
      csvReader.readNext();
      while ((nextRecord = csvReader.readNext()) != null) {
        Long userId = Long.parseLong(nextRecord[0]);
        String firstName = nextRecord[1];
        String lastName = nextRecord[2];
        String username = nextRecord[3];
        String password = nextRecord[4];
        String dateOfBirthStr = nextRecord[5];
        String address = nextRecord[6];
        boolean isActive = Boolean.parseBoolean(nextRecord[7]);
        assertTrainee(trainees.get(index), userId, firstName, lastName, username, password, dateOfBirthStr, address, isActive);
        index++;
      }
      assertEquals(index, trainees.size(), "Number of trainees should match the CSV records.");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void assertTrainee(Trainee trainee, Long userId, String firstName, String lastName,
      String username, String password, String dateOfBirth, String address, boolean isActive) throws ParseException {
    assertEquals(userId, trainee.getUserId(), "User ID does not match.");
    assertEquals(firstName, trainee.getFirstName(), "First name does not match.");
    assertEquals(lastName, trainee.getLastName(), "Last name does not match.");
    assertEquals(username, trainee.getUsername(), "Username does not match.");
    assertEquals(password, trainee.getPassword(), "Password does not match.");
    assertEquals(dateFormat.parse(dateOfBirth), trainee.getDateOfBirth(), "Date of birth does not match.");
    assertEquals(address, trainee.getAddress(), "Address does not match.");
    assertEquals(isActive, trainee.isActive(), "Active status does not match.");
  }
}