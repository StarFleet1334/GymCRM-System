package com.demo.folder.service;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
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

import static com.demo.folder.utils.StorageUtil.TRAINEES_NAMESPACE;

@Service
public class TraineeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
  private TraineeDAO traineeDAO;
  private StorageBean storageBean;

  @Autowired
  public TraineeService(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  @Autowired
  public void setTraineeDAO(TraineeDAO traineeDAO) {
    this.traineeDAO = traineeDAO;
  }

  public void createTrainee(Trainee trainee) {
    if (trainee.getFirstName() == null || trainee.getLastName() == null) {
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
    traineeDAO.create(trainee);

  }

  public TraineeDAO getTraineeDAO() {
    return traineeDAO;
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

  public void update(Long userId, Trainee trainee) {
    Collection<Trainee> allTrainees = getAllTrainees();
    Optional<Trainee> existingTraineeOptional = allTrainees.stream()
        .filter(t -> t.getUserId().equals(userId))
        .findFirst();
    if (existingTraineeOptional.isEmpty()) {
      LOGGER.warn("Trainee with userId {} not found. Update operation failed.", userId);
      return;
    }
    Trainee existingTrainee = existingTraineeOptional.get();
    if (!Objects.equals(trainee.getFirstName(), "Unknown")) {
      existingTrainee.setFirstName(trainee.getFirstName());
    }
    if (!Objects.equals(trainee.getLastName(), "Unknown")) {
      existingTrainee.setLastName(trainee.getLastName());
    }
    if (!Objects.equals(trainee.getFirstName(), "Unknown") || !Objects.equals(trainee.getLastName(),
        "Unknown")) {
      existingTrainee.setUsername(Generator.generateUserName(
          !Objects.equals(trainee.getFirstName(), "Unknown") ? trainee.getFirstName() : existingTrainee.getFirstName(),
          !Objects.equals(trainee.getLastName(), "Unknown") ? trainee.getLastName() : existingTrainee.getLastName()
      ));
    }
    if (trainee.getPassword() != null) {
      existingTrainee.setPassword(trainee.getPassword());
    }
    if (trainee.getDateOfBirth() != null) {
      existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
    }
    if (!Objects.equals(trainee.getAddress(), "Unknown")) {
      existingTrainee.setAddress(trainee.getAddress());
    }
    // For boolean, you may have to check if it's explicitly set or use some default logic
    existingTrainee.setActive(trainee.isActive());
    storageBean.getByNameSpace(TRAINEES_NAMESPACE).put(userId, existingTrainee);
    LOGGER.info("Trainee with userId {} successfully updated: {}", userId, existingTrainee);
  }
}
