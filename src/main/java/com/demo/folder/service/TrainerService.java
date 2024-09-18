package com.demo.folder.service;

import com.demo.folder.entity.Trainer;
import com.demo.folder.repository.TrainerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
  @Autowired
  private TrainerRepository trainerRepository;

  @Transactional
  public Trainer createTrainer(Trainer trainer) {
    LOGGER.info("Creating new Trainer with username: {}", trainer.getUser().getUsername());
    trainerRepository.save(trainer);
    LOGGER.debug("Trainer created: {}", trainer);
    return trainer;
  }

  @Transactional(readOnly = true)
  public Trainer findTrainerByUsername(String username) {
    LOGGER.info("Finding Trainer by username: {}", username);
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }

    Trainer trainer = trainerRepository.findByUsername(username);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with username " + username + " not found.");
    }
    return trainer;
  }

  @Transactional
  public void activateTrainer(Long trainerId) {
    LOGGER.info("Activating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, true);
  }

  @Transactional
  public void deactivateTrainer(Long trainerId) {
    LOGGER.info("Deactivating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, false);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAllTrainers() {
    LOGGER.info("Fetching all Trainers.");
    List<Trainer> trainers = trainerRepository.findAll();

    if (trainers.isEmpty()) {
      throw new EntityNotFoundException("No trainers found.");
    }
    return trainers;
  }

  @Transactional
  public void updateTrainer(Trainer trainer) {
    String username = trainer.getUser().getUsername();
    LOGGER.info("Updating Trainer with username: {}", trainer.getUser().getUsername());
    Trainer existingTrainer = trainerRepository.findByUsername(username);
    if (existingTrainer == null) {
      throw new EntityNotFoundException("Trainer with username " + username + " not found.");
    }
    trainerRepository.save(trainer);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(List<Trainer> assignedTrainers) {
    LOGGER.info("Fetching unassigned Trainers.");
    if (assignedTrainers == null || assignedTrainers.isEmpty()) {
      throw new IllegalArgumentException("Assigned trainers list cannot be null or empty.");
    }
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }


}
