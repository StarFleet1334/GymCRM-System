package com.demo.folder.service;

import com.demo.folder.actions.trainer.TrainerUpdateImpl;
import com.demo.folder.actions.trainer.TrainerUpdater;
import com.demo.folder.dao.TrainerDAO;
import com.demo.folder.model.Trainer;
import com.demo.folder.utils.Generator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class TrainerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
  private TrainerDAO trainerDAO;


  @Autowired
  public void setTrainerDAO(TrainerDAO trainerDAO) {
    this.trainerDAO = trainerDAO;
  }

  public Trainer getTrainer(Long userId) {
    return trainerDAO.read(userId);
  }

  public List<Trainer> getAllTrainers() {
    return trainerDAO.getAll();
  }

  public void createTrainer(Trainer trainer) {
    if (trainer.getFirstName().isEmpty() || trainer.getLastName().isEmpty()) {
      LOGGER.warn("Cannot create trainer with null firstName or lastName");
      throw new IllegalArgumentException("First name and last name must not be null");
    }
    List<Trainer> existingTrainers = trainerDAO.getAll().stream()
        .filter(t -> t.getFirstName().equalsIgnoreCase(trainer.getFirstName())
            && t.getLastName().equalsIgnoreCase(trainer.getLastName()))
        .toList();
    if (!existingTrainers.isEmpty()) {
      String baseUsername = trainer.getUsername();
      AtomicInteger serialNumber = new AtomicInteger(1);
      Optional<Trainer> duplicate;
      do {
        String newUsername = baseUsername + serialNumber.getAndIncrement();
        duplicate = existingTrainers.stream()
            .filter(t -> t.getUsername().equalsIgnoreCase(newUsername))
            .findFirst();
        if (duplicate.isEmpty()) {
          trainer.setUsername(newUsername);
          break;
        }
      } while (duplicate.isPresent());
    }
    // When creating it auto-generates password for trainer!
    trainer.setPassword(Generator.generatePassword());
    trainerDAO.create(trainer);
  }

  public void update(Long userId, Trainer updatedTrainer) {
    Collection<Trainer> allTrainers = getAllTrainers();
    Optional<Trainer> existingTraineeOptional = allTrainers.stream()
        .filter(t -> t.getUserId().equals(userId))
        .findFirst();
    if (existingTraineeOptional.isEmpty()) {
      LOGGER.warn("Trainer with userId {} not found. Update operation failed.", userId);
      return;
    }
    Trainer existingTrainer = existingTraineeOptional.get();
    List<TrainerUpdater> updaterList = List.of(
        TrainerUpdateImpl.UPDATE_USERNAME,
        TrainerUpdateImpl.UPDATE_LAST_NAME,
        TrainerUpdateImpl.UPDATE_USERNAME,
        TrainerUpdateImpl.UPDATE_PASSWORD,
        TrainerUpdateImpl.UPDATE_SPECIALIZATION,
        TrainerUpdateImpl.UPDATE_ACTIVE_STATUS
    );
    updaterList.forEach(updater -> updater.update(existingTrainer, updatedTrainer));
    trainerDAO.update(existingTrainer);
    LOGGER.info("Trainee with userId {} successfully updated: {}", userId, existingTrainer);
  }
}