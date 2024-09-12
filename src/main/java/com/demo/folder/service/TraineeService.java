package com.demo.folder.service;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TraineeService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;

  @Autowired
  public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
    this.traineeRepository = traineeRepository;
    this.trainerRepository = trainerRepository;
    LOGGER.info("TraineeService initialized.");

  }

  @Transactional
  public void createTrainee(Trainee trainee) {
    LOGGER.info("Creating new Trainee with username: {}", trainee.getUser().getUsername());
    traineeRepository.save(trainee);
    LOGGER.debug("Trainee created: {}", trainee);

  }

  @Transactional
  public Trainee findTraineeByUsername(String username) {
    LOGGER.info("Finding Trainee by username: {}", username);
    return traineeRepository.findByUsername(username);
  }

  @Transactional(readOnly = true)
  public List<Trainee> getAllTrainees() {
    LOGGER.info("Fetching all Trainees.");
    return traineeRepository.findAll();
  }


  @Transactional
  public void activateTrainee(Long traineeId) {
    LOGGER.info("Activating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, true);
  }

  @Transactional
  public void deactivateTrainee(Long traineeId) {
    LOGGER.info("Deactivating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, false);
  }

  @Transactional
  public void deleteTraineeById(Long traineeId) {
    LOGGER.info("Deleting Trainee with ID: {}", traineeId);
    traineeRepository.deleteById(traineeId);
  }

  @Transactional
  public void updateTrainee(Trainee trainee) {
    LOGGER.info("Updating Trainee with username: {}", trainee.getUser().getUsername());
    traineeRepository.save(trainee); // Update trainee
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAssignedTrainers(Trainee trainee) {
    LOGGER.info("Fetching assigned trainers for Trainee: {}", trainee.getUser().getUsername());
    return trainee.getTrainers();
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(Trainee trainee) {
    LOGGER.info("Fetching unassigned trainers for Trainee: {}", trainee.getUser().getUsername());
    List<Trainer> assignedTrainers = trainee.getTrainers();
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }

}
