package com.demo.folder.service;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
import com.demo.folder.storage.StorageBean;
import com.demo.folder.utils.Generator;
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
    trainee.setPassword(Generator.generatePassword());
    trainee.setUsername(Generator.generateUserName(trainee.getFirstName(), trainee.getLastName()));
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
    existingTrainee.setFirstName(trainee.getFirstName());
    existingTrainee.setLastName(trainee.getLastName());
    existingTrainee.setUsername(trainee.getUsername());
    existingTrainee.setPassword(trainee.getPassword());
    existingTrainee.setActive(trainee.isActive());
    existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
    existingTrainee.setAddress(trainee.getAddress());
    storageBean.getByNameSpace(TRAINEES_NAMESPACE).put(userId, existingTrainee);
    LOGGER.info("Trainee with userId {} successfully updated: {}", userId, existingTrainee);
  }
}
