package com.demo.folder.service;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import jakarta.persistence.EntityNotFoundException;
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
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }

    Trainee trainee = traineeRepository.findByUsername(username);

    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    return trainee;
  }

  @Transactional(readOnly = true)
  public List<Trainee> getAllTrainees() {
    LOGGER.info("Fetching all Trainees.");
    List<Trainee> trainees = traineeRepository.findAll();
    if (trainees.isEmpty()) {
      throw new EntityNotFoundException("No trainees found.");
    }
    return trainees;
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
    String username = trainee.getUser().getUsername();
    LOGGER.info("Updating Trainee with username: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    traineeRepository.save(trainee);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAssignedTrainers(Trainee trainee) {
    String username = trainee.getUser().getUsername();
    LOGGER.info("Fetching assigned trainers for Trainee: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    return trainee.getTrainers();
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(Trainee trainee) {
    String username = trainee.getUser().getUsername();
    LOGGER.info("Fetching unassigned trainers for Trainee: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    List<Trainer> assignedTrainers = trainee.getTrainers();
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }

}
