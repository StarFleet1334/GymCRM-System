package com.demo.folder.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileUtils.class);
  private static final String EXIT_MESSAGE= "Exit";
  public static void showTraineeServiceOptions() {
    LOGGER.info("1. Create Trainee");
    LOGGER.info("2. Select Trainee by ID");
    LOGGER.info("3. Select All Trainees");
    LOGGER.info("4. Delete Trainee by ID");
    LOGGER.info("5. Update Trainee by ID");
    LOGGER.info("6. " + EXIT_MESSAGE);
  }

  public static void showTrainerServiceOptions() {
    LOGGER.info("1. Create Trainer");
    LOGGER.info("2. Update Trainer");
    LOGGER.info("3. Select Trainer by ID");
    LOGGER.info("4. Select All Trainers");
    LOGGER.info("5. " + EXIT_MESSAGE);
  }

  public static void showTrainingServiceOptions() {
    LOGGER.info("1. Create Training");
    LOGGER.info("2. Select Training by ID");
    LOGGER.info("3. Select All Trainings");
    LOGGER.info("4. "+ EXIT_MESSAGE);
  }
}
