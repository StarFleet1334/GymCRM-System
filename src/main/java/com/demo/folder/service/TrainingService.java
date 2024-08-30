package com.demo.folder.service;

import com.demo.folder.dao.TrainingDAO;
import com.demo.folder.model.Training;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.demo.folder.utils.StorageUtil.*;


@Service
public class TrainingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);
  private TrainingDAO trainingDAO;
  private StorageBean storageBean;

  @Autowired
  public TrainingService(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  @Autowired
  public void setTrainingDAO(TrainingDAO trainingDAO) {
    this.trainingDAO = trainingDAO;
  }


  public Training getTraining(Long trainingId) {
    return trainingDAO.read(trainingId);
  }

  public List<Training> getAllTrainings() {
    return trainingDAO.getAll();
  }

  public void createTraining(Training training) {
    if (training == null || training.getTrainingName() == null || training.getTrainingType() == null
        ||
        training.getTrainingDate() == null || training.getTrainingDuration() <= 0) {
      LOGGER.error("Invalid training data. Training creation failed.");
      return;
    }
    Date currentDate = new Date();
    if (training.getTrainingDate() == null) {
      if (training.getTrainingDate().before(currentDate)) {
        LOGGER.error("Training date {} is in the past. Training creation failed.",
            training.getTrainingDate());
        return;
      }
    }
    if (training.getTrainingDuration() < 0) {
      LOGGER.error("Training duration cannot be negative. Training creation failed.");
      return;
    }
    Map<Long, Object> trainings = storageBean.getByNameSpace(TRAININGS_NAMESPACE);
    Map<Long, Object> trainees = storageBean.getByNameSpace(TRAINEES_NAMESPACE);
    if (isTrainerBusy(training)) {
      LOGGER.error(
          "Trainer with ID {} is already busy during the specified training period. Training creation failed.",
          training.getTrainerId());
      return;
    }

    trainings.put(training.getTrainingId(), training);
    LOGGER.info("Training with ID {} successfully created: {}", training.getTrainingId(), training);
  }

  private boolean isTrainerBusy(Training training) {
    Map<Long, Object> trainings = storageBean.getByNameSpace(TRAININGS_NAMESPACE);
    for (Object obj : trainings.values()) {
      Training existingTraining = (Training) obj;
      if (existingTraining.getTrainerId().equals(training.getTrainerId())) {
        Date existingStart = existingTraining.getTrainingDate();
        Date existingEnd = getEndDate(existingStart, existingTraining.getTrainingDuration());
        Date newStart = training.getTrainingDate();
        Date newEnd = getEndDate(newStart, training.getTrainingDuration());
        if (newStart.before(existingEnd) && existingStart.before(newEnd)) {
          return true; // this means the trainer is busy
        }
      }
    }
    return false; // here also means trainer is busy
  }

  private Date getEndDate(Date startDate, int durationDays) {
    long durationMillis = TimeUnit.DAYS.toMillis(durationDays);
    return new Date(startDate.getTime() + durationMillis);
  }
}
