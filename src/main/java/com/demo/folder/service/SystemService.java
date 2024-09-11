package com.demo.folder.service;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.Training;
import com.demo.folder.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemService {

  @Autowired
  private TrainerService trainerService;

  @Autowired
  private TraineeService traineeService;

  @Autowired
  private TrainingRepository trainingRepository;

  public List<Trainer> getAllTrainers() {
    return trainerService.getAllTrainers();
  }

  public List<Trainee> getAllTrainees() {
    return traineeService.getAllTrainees();
  }

  public List<Training> getAllTrainings() {
    return trainingRepository.findAll();
  }

  public List<Training> getTrainingsFromDate(Date date) {
    return trainingRepository.findByTrainingDateAfter(date);
  }

  public List<Trainer> getUnassignedTrainers() {
    List<Trainer> allTrainers = trainerService.getAllTrainers();
    List<Trainer> assignedTrainers = trainingRepository.findAll()
        .stream()
        .map(Training::getTrainer)
        .toList();

    return allTrainers.stream()
        .filter(trainer -> !assignedTrainers.contains(trainer))
        .collect(Collectors.toList());
  }
}
