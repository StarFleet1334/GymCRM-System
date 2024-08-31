package com.demo.folder.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.folder.model.Training;
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
class DataInitializerTrainingTest {

  @Autowired
  private SystemFacade systemFacade;
  private static final String TRAINING_PATH = "src/main/resources/data/training.csv";
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


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