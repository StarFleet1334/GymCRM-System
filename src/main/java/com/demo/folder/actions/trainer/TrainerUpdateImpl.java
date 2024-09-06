package com.demo.folder.actions.trainer;

import com.demo.folder.utils.Generator;
import java.util.Objects;

public class TrainerUpdateImpl {

  public static final TrainerUpdater UPDATE_FIRST_NAME = (existingTrainer, updatedTrainer) -> {
    if (!Objects.equals(updatedTrainer.getFirstName(), "Unknown")) {
      existingTrainer.setFirstName(updatedTrainer.getFirstName());
    }
  };

  public static final TrainerUpdater UPDATE_LAST_NAME = (existingTrainer, updatedTrainer) -> {
    if (!Objects.equals(updatedTrainer.getLastName(), "Unknown")) {
      existingTrainer.setLastName(updatedTrainer.getLastName());
    }
  };

  public static final TrainerUpdater UPDATE_USERNAME = (existingTrainer, updatedTrainer) -> {
    if (!Objects.equals(updatedTrainer.getFirstName(), "Unknown") ||
        !Objects.equals(updatedTrainer.getLastName(), "Unknown")) {
      existingTrainer.setUsername(
          Generator.generateUserName(
              !Objects.equals(updatedTrainer.getFirstName(), "Unknown") ? updatedTrainer.getFirstName() : existingTrainer.getFirstName(),
              !Objects.equals(updatedTrainer.getLastName(), "Unknown") ? updatedTrainer.getLastName() : existingTrainer.getLastName()
          )
      );
    }
  };

  public static final TrainerUpdater UPDATE_PASSWORD = (existingTrainer, updatedTrainer) -> {
    if (updatedTrainer.getPassword() != null) {
      existingTrainer.setPassword(updatedTrainer.getPassword());
    }
  };

  public static final TrainerUpdater UPDATE_SPECIALIZATION = (existingTrainer, updatedTrainer) -> {
    if (!Objects.equals(updatedTrainer.getSpecialization(), "Unknown")) {
      existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
    }
  };

  public static final TrainerUpdater UPDATE_ACTIVE_STATUS = (existingTrainer, updatedTrainer) -> {
    existingTrainer.setActive(updatedTrainer.isActive());
  };
}
