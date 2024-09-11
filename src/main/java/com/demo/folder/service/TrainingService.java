package com.demo.folder.service;

import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.Training;
import com.demo.folder.repository.TrainingRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService {

  @Autowired
  private TrainingRepository trainingRepository;

  @Transactional
  public List<Training> getTrainingsForTraineeByCriteria(Long traineeId, LocalDate fromDate,
      LocalDate toDate, String trainerName, String trainingType) {
    return trainingRepository.findTrainingsForTraineeByCriteria(traineeId, fromDate, toDate,
        trainerName, trainingType);
  }

  @Transactional
  public List<Training> getTrainingsForTrainerByCriteria(Long trainerId, LocalDate fromDate,
      LocalDate toDate, String traineeName) {
    return trainingRepository.findTrainingsForTrainerByCriteria(trainerId, fromDate, toDate,
        traineeName);
  }

  @Transactional
  public void saveTraining(Training training) {
    trainingRepository.save(training);
  }

  @Transactional(readOnly = true)
  public List<Training> getAllTrainings() {
    return trainingRepository.findAll();
  }
}

