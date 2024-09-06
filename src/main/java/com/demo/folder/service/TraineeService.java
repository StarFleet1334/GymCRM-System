package com.demo.folder.service;

import com.demo.folder.actions.trainee.TraineeUpdateImpl;
import com.demo.folder.actions.trainee.TraineeUpdater;
import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
import com.demo.folder.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TraineeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
  private TraineeDAO traineeDAO;


  @Autowired
  public void setTraineeDAO(TraineeDAO traineeDAO) {
    this.traineeDAO = traineeDAO;
  }

  public void createTrainee(Trainee trainee) {
    if (trainee.getFirstName().isEmpty() || trainee.getLastName().isEmpty()) {
      LOGGER.warn("Cannot create trainee with null firstName or lastName");
      throw new IllegalArgumentException("First name and last name must not be null");
    }
    LOGGER.info("New Trainee got created");
    List<Trainee> existingTrainees = traineeDAO.getAll().stream()
        .filter(t -> t.getFirstName().equalsIgnoreCase(trainee.getFirstName())
            && t.getLastName().equalsIgnoreCase(trainee.getLastName()))
        .toList();

    if (!existingTrainees.isEmpty()) {
      String baseUsername = trainee.getUsername();
      AtomicInteger serialNumber = new AtomicInteger(1);
      Optional<Trainee> duplicate;
      do {
        String newUsername = baseUsername + serialNumber.getAndIncrement();
        duplicate = existingTrainees.stream()
            .filter(t -> t.getUsername().equalsIgnoreCase(newUsername))
            .findFirst();
        if (duplicate.isEmpty()) {
          trainee.setUsername(newUsername);
          break;
        }
      } while (duplicate.isPresent());
    }
    // When creating it auto-generates password for trainee!
    trainee.setPassword(Generator.generatePassword());
    traineeDAO.create(trainee);

  }

  public Trainee getTrainee(Long userId) {
    return traineeDAO.read(userId);
  }

  public List<Trainee> getAllTrainees() {
    return traineeDAO.getAll();
  }

  public void deleteTrainee(Long userId) {
    Collection<Trainee> allTrainees = getAllTrainees();
    Optional<Trainee> existingTraineeOptional = allTrainees.stream()
        .filter(t -> t.getUserId().equals(userId))
        .findFirst();
    if (existingTraineeOptional.isEmpty()) {
      LOGGER.warn("Trainee with userId {} not found. delete operation failed.", userId);
      return;
    }
    traineeDAO.delete(userId);
    LOGGER.info("Trainee with id {} deleted", userId);
  }

  public void update(Long userId, Trainee updatedTrainee) {
    Collection<Trainee> allTrainees = getAllTrainees();
    Optional<Trainee> existingTraineeOptional = allTrainees.stream()
        .filter(t -> t.getUserId().equals(userId))
        .findFirst();
    if (existingTraineeOptional.isEmpty()) {
      LOGGER.warn("Trainee with userId {} not found. Update operation failed.", userId);
      return;
    }
    Trainee existingTrainee = existingTraineeOptional.get();

    List<TraineeUpdater> updaters = List.of(
        TraineeUpdateImpl.UPDATE_FIRST_NAME,
        TraineeUpdateImpl.UPDATE_LAST_NAME,
        TraineeUpdateImpl.UPDATE_USERNAME,
        TraineeUpdateImpl.UPDATE_PASSWORD,
        TraineeUpdateImpl.UPDATE_DOB,
        TraineeUpdateImpl.UPDATE_ADDRESS,
        TraineeUpdateImpl.UPDATE_TRAINING,
        TraineeUpdateImpl.UPDATE_ACTIVE_STATUS
    );

    updaters.forEach(updater -> updater.update(existingTrainee, updatedTrainee));
    traineeDAO.update(existingTrainee);
    LOGGER.info("Trainee with userId {} successfully updated: {}", userId, existingTrainee);
  }
}
