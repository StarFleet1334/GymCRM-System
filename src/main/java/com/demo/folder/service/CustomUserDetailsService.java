package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
  private final TraineeService traineeService;
  private final TrainerService trainerService;

  @Autowired
  public CustomUserDetailsService(TraineeService traineeService, TrainerService trainerService) {
    this.traineeService = traineeService;
    this.trainerService = trainerService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOGGER.info("LoadUserByUsername called with username: {}", username);

    if (username == null || username.isEmpty()) {
      LOGGER.error("Username is null or empty");
      throw new UsernameNotFoundException("Username cannot be null or empty");
    }

    Trainer trainer = trainerService.findTrainerByUsername(username);
    Trainee trainee = traineeService.findTraineeByUsername(username);

    if (trainee == null && trainer == null) {
      LOGGER.warn("User not found with username: {}", username);
      throw new UsernameNotFoundException("User not found");
    }

    if (trainee != null) {
      LOGGER.info("Trainee found, returning UserDetails for trainee: {}", trainee.getUser().getUsername());
      return new org.springframework.security.core.userdetails.User(
          trainee.getUser().getUsername(), trainee.getUser().getPassword(), new ArrayList<>());
    }

    LOGGER.info("Trainer found, returning UserDetails for trainer: {}", trainer.getUser().getUsername());
    return new org.springframework.security.core.userdetails.User(
        trainer.getUser().getUsername(), trainer.getUser().getPassword(), new ArrayList<>());
  }

}

