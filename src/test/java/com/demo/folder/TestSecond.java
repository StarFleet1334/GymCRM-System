package com.demo.folder;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.system.SystemFacade;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSecond {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestFirst.class);
  @Autowired
  private SystemFacade systemFacade;
  private static final String TRAINEE_PATH = "src/main/resources/data/trainee.csv";
  private static final String TRAINER_PATH = "src/main/resources/data/trainer.csv";
  private static final String TRAINING_PATH = "src/main/resources/data/training.csv";

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


  @Test
  public void multipleTraineeToTrainingCheck() {
    int size = systemFacade.getTrainingService().getAllTrainings().size();
    Calendar futureDate = Calendar.getInstance();
    futureDate.add(Calendar.DATE, 1);

    Training training = new Training();
    training.setTrainingName("Boxing");
    training.setTrainingDate(futureDate.getTime());

    Trainer trainer1 = new Trainer();
    trainer1.setFirstName("saba");
    trainer1.setLastName("Dumbadze");
    training.setTrainerId(trainer1.getUserId());

    Training training1 = new Training();
    training1.setTrainingName("Boxing");

    Trainer trainer2 = new Trainer();
    trainer2.setFirstName("Ilia");
    trainer2.setLastName("Lataria");
    training1.setTrainerId(trainer2.getUserId());

    training1.setTrainingDate(futureDate.getTime());

    systemFacade.getTrainerService().createTrainer(trainer1);
    systemFacade.getTrainerService().createTrainer(trainer2);
    systemFacade.getTrainingService().createTraining(training);
    systemFacade.getTrainingService().createTraining(training1);

    systemFacade.getTrainerService().getAllTrainers().forEach(Trainer::describe);
    systemFacade.getTrainingService().getAllTrainings().forEach(Training::describe);
    assertEquals(6,systemFacade.getTrainingService().getTraining((long) (size+1)).getTrainerId());
    assertEquals(7,systemFacade.getTrainingService().getTraining((long) (size+2)).getTrainerId());
  }

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
    } catch (IOException  e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void initialDataTrainerCheck() {
    List<Trainer> lst = systemFacade.getTrainerService().getAllTrainers();
    try (CSVReader csvReader = new CSVReader(new FileReader(TRAINER_PATH))) {
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
        assertTrainer(lst.get(index), userId, firstName, lastName, username, password, specialization, isActive);
        index++;
      }
      assertEquals(index, lst.size(), "Number of trainers should match the CSV records.");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void initialDataTrainingCheck() throws ParseException {
    List<Training> trainings = systemFacade.getTrainingService().getAllTrainings();
    try (CSVReader csvReader = new CSVReader(new FileReader(TRAINING_PATH))) {
      String[] nextRecord;
      int index = 0;
      csvReader.readNext();
      while ((nextRecord = csvReader.readNext()) != null) {
        Long trainingId = Long.parseLong(nextRecord[0]);
        Long traineeId = Long.parseLong(nextRecord[1]);
        Long trainerId = Long.parseLong(nextRecord[2]);
        String trainingName = nextRecord[3];
        String trainingType = nextRecord[4];
        String trainingDate = nextRecord[5];
        int trainingDuration = Integer.parseInt(nextRecord[6]);
        assertTraining(trainings.get(index), trainingId, traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
        index++;
      }
      assertEquals(index, trainings.size(), "Number of trainings should match the CSV records.");
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

  private void assertTraining(Training training, Long trainingId, Long traineeId, Long trainerId,
      String trainingName, String trainingType, String trainingDate, int trainingDuration) throws ParseException {
    assertEquals(trainingId, training.getTrainingId(), "Training ID does not match.");
    assertEquals(traineeId, training.getTraineeId(), "Trainee ID does not match.");
    assertEquals(trainerId, training.getTrainerId(), "Trainer ID does not match.");
    assertEquals(trainingName, training.getTrainingName(), "Training name does not match.");
    assertEquals(trainingType, training.getTrainingType().toString(), "Training type does not match."); // Assuming getTrainingType() returns an enum
    assertEquals(dateFormat.parse(trainingDate), training.getTrainingDate(), "Training date does not match.");
    assertEquals(trainingDuration, training.getTrainingDuration(), "Training duration does not match.");
  }
}
