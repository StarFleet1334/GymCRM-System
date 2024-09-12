package com.demo.folder.service;

import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.Training;
import com.demo.folder.repository.TrainingRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);
  @Autowired
  private TrainingRepository trainingRepository;

  @Transactional
  public List<Training> getTrainingsForTraineeByCriteria(Long traineeId, LocalDate fromDate,
      LocalDate toDate, String trainerName, String trainingType) {
    LOGGER.info("Fetching trainings for trainee with ID: {}, from: {}, to: {}, trainerName: {}, trainingType: {}",
        traineeId, fromDate, toDate, trainerName, trainingType);
    return trainingRepository.findTrainingsForTraineeByCriteria(traineeId, fromDate, toDate,
        trainerName, trainingType);
  }

  @Transactional
  public List<Training> getTrainingsForTrainerByCriteria(Long trainerId, LocalDate fromDate,
      LocalDate toDate, String traineeName) {
    LOGGER.info("Fetching trainings for trainer with ID: {}, from: {}, to: {}, traineeName: {}",
        trainerId, fromDate, toDate, traineeName);
    return trainingRepository.findTrainingsForTrainerByCriteria(trainerId, fromDate, toDate,
        traineeName);
  }

  @Transactional
  public void saveTraining(Training training) {
    LOGGER.info("Saving training with name: {}", training.getTrainingName());
    trainingRepository.save(training);
  }

  @Transactional(readOnly = true)
  public List<Training> getAllTrainings() {
    LOGGER.info("Fetching all trainings");

    return trainingRepository.findAll();
  }
}

