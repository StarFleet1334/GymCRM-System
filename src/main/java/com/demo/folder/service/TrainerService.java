package com.demo.folder.service;

import com.demo.folder.dao.TrainerDAO;
import com.demo.folder.model.Trainer;
import com.demo.folder.storage.StorageBean;
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

import static com.demo.folder.utils.StorageUtil.TRAINERS_NAMESPACE;

@Service
public class TrainerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
  private TrainerDAO trainerDAO;
  private StorageBean storageBean;

  @Autowired
  public TrainerService(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  @Autowired
  public void setTrainerDAO(TrainerDAO trainerDAO) {
    this.trainerDAO = trainerDAO;
  }

  public TrainerDAO getTrainerDAO() {
    return trainerDAO;
  }

  public Trainer getTrainer(Long userId) {
    return trainerDAO.read(userId);
  }

  public List<Trainer> getAllTrainers() {
    return trainerDAO.getAll();
  }

  public void createTrainer(Trainer trainer) {
    if (trainer.getFirstName() == null || trainer.getLastName() == null) {
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
    trainerDAO.create(trainer);
  }

  public void update(Long userId, Trainer trainer) {
    Collection<Trainer> allTrainers = getAllTrainers();
    Optional<Trainer> existingTraineeOptional = allTrainers.stream()
        .filter(t -> t.getUserId().equals(userId))
        .findFirst();
    if (existingTraineeOptional.isEmpty()) {
      LOGGER.warn("Trainer with userId {} not found. Update operation failed.", userId);
      return;
    }
    Trainer existingTrainer = existingTraineeOptional.get();
    if (!Objects.equals(trainer.getFirstName(), "Unknown")) {
      existingTrainer.setFirstName(trainer.getFirstName());
    }
    if (!Objects.equals(trainer.getLastName(), "Unknown")) {
      existingTrainer.setLastName(trainer.getLastName());
    }
    if (!Objects.equals(trainer.getFirstName(), "Unknown") || !Objects.equals(trainer.getLastName(),
        "Unknown")) {
      existingTrainer.setUsername(Generator.generateUserName(
          !Objects.equals(trainer.getFirstName(), "Unknown") ? trainer.getFirstName() : existingTrainer.getFirstName(),
          !Objects.equals(trainer.getLastName(), "Unknown") ? trainer.getLastName() : existingTrainer.getLastName()
      ));
    }
    if (trainer.getPassword() != null) {
      existingTrainer.setPassword(trainer.getPassword());
    }
    existingTrainer.setActive(trainer.isActive());

    if (!Objects.equals(trainer.getSpecialization(), "Unknown")) {
      existingTrainer.setSpecialization(trainer.getSpecialization());
    }

    storageBean.getByNameSpace(TRAINERS_NAMESPACE).put(userId, existingTrainer);
    LOGGER.info("Trainee with userId {} successfully updated: {}", userId, existingTrainer);
  }
}