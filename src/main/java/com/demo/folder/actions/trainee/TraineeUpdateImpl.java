package com.demo.folder.actions.trainee;

import com.demo.folder.utils.Generator;
import java.util.Objects;

public class TraineeUpdateImpl {
  public static final TraineeUpdater UPDATE_FIRST_NAME = (existingTrainee, updatedTrainee) -> {
    if (!Objects.equals(updatedTrainee.getFirstName(), "Unknown")) {
      existingTrainee.setFirstName(updatedTrainee.getFirstName());
    }
  };

  public static final TraineeUpdater UPDATE_LAST_NAME = (existingTrainee, updatedTrainee) -> {
    if (!Objects.equals(updatedTrainee.getLastName(), "Unknown")) {
      existingTrainee.setLastName(updatedTrainee.getLastName());
    }
  };

  public static final TraineeUpdater UPDATE_USERNAME = (existingTrainee, updatedTrainee) -> {
    if (!Objects.equals(updatedTrainee.getFirstName(), "Unknown") || !Objects.equals(updatedTrainee.getLastName(), "Unknown")) {
      existingTrainee.setUsername(
          Generator.generateUserName(
              !Objects.equals(updatedTrainee.getFirstName(), "Unknown") ? updatedTrainee.getFirstName() : existingTrainee.getFirstName(),
              !Objects.equals(updatedTrainee.getLastName(), "Unknown") ? updatedTrainee.getLastName() : existingTrainee.getLastName()
          )
      );
    }
  };

  public static final TraineeUpdater UPDATE_PASSWORD = (existingTrainee, updatedTrainee) -> {
    if (updatedTrainee.getPassword() != null) {
      existingTrainee.setPassword(updatedTrainee.getPassword());
    }
  };

  public static final TraineeUpdater UPDATE_DOB = (existingTrainee, updatedTrainee) -> {
    if (updatedTrainee.getDateOfBirth() != null) {
      existingTrainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
    }
  };

  public static final TraineeUpdater UPDATE_ADDRESS = (existingTrainee, updatedTrainee) -> {
    if (!Objects.equals(updatedTrainee.getAddress(), "Unknown")) {
      existingTrainee.setAddress(updatedTrainee.getAddress());
    }
  };

  public static final TraineeUpdater UPDATE_TRAINING = (existingTrainee, updatedTrainee) -> {
    if (!updatedTrainee.getTraining().isEmpty()) {
      existingTrainee.setTraining(updatedTrainee.getTraining());
    }
  };

  public static final TraineeUpdater UPDATE_ACTIVE_STATUS = (existingTrainee, updatedTrainee) -> {
    existingTrainee.setActive(updatedTrainee.isActive());
  };
}
