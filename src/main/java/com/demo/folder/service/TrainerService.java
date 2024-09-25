package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTrainerProfileRequestDTO;
import com.demo.folder.entity.dto.response.TraineeProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingTypeRepository;
import com.demo.folder.utils.Generator;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
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

  @Autowired
  private TrainingTypeRepository trainingTypeRepository;


  @Transactional
  public Trainer createTrainer(Trainer trainer) {
//    LOGGER.info("Creating new Trainer with username: {}", trainer.getUser().getUsername());
    trainerRepository.save(trainer);
//    LOGGER.debug("Trainer created: {}", trainer);
    return trainer;
  }

  @Transactional
  public Trainer createTrainer(TrainerRequestDTO trainerRequestDTO) {
    // Check if TrainingType exists in the database
    Optional<TrainingType> existingTrainingType = trainingTypeRepository.findById(trainerRequestDTO.getTrainingTypeId());

    TrainingType trainingType;
    if (existingTrainingType.isPresent()) {
      // If the TrainingType exists, use the existing one
      trainingType = existingTrainingType.get();
    } else {
      return null;
    }

    // Create User entity for the Trainer
    User user = new User();
    user.setFirstName(trainerRequestDTO.getFirstName());
    user.setLastName(trainerRequestDTO.getLastName());
    user.setUsername(Generator.generateUserName(trainerRequestDTO.getFirstName(), trainerRequestDTO.getLastName()));
    user.setPassword(Generator.generatePassword());
    user.setActive(trainerRequestDTO.isActive());

    // Create Trainer entity
    Trainer trainer = new Trainer();
    trainer.setSpecialization(trainingType);
    trainer.setUser(user);

    // Save the Trainer
    trainerRepository.save(trainer);

    return trainer;
  }


  @Transactional(readOnly = true)
  public Trainer findTrainerByUsername(String username) {
//    LOGGER.info("Finding Trainer by username: {}", username);
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
//    LOGGER.info("Activating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, true);
  }

  @Transactional
  public void deactivateTrainer(Long trainerId) {
//    LOGGER.info("Deactivating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, false);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAllTrainers() {
//    LOGGER.info("Fetching all Trainers.");
    List<Trainer> trainers = trainerRepository.findAll();

    if (trainers.isEmpty()) {
      throw new EntityNotFoundException("No trainers found.");
    }
    return trainers;
  }

  @Transactional
  public void updateTrainer(Trainer trainer) {
    String username = trainer.getUser().getUsername();
//    LOGGER.info("Updating Trainer with username: {}", trainer.getUser().getUsername());
    Trainer existingTrainer = trainerRepository.findByUsername(username);
    if (existingTrainer == null) {
      throw new EntityNotFoundException("Trainer with username " + username + " not found.");
    }
    trainerRepository.save(trainer);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(List<Trainer> assignedTrainers) {
//    LOGGER.info("Fetching unassigned Trainers.");
    if (assignedTrainers == null || assignedTrainers.isEmpty()) {
      throw new IllegalArgumentException("Assigned trainers list cannot be null or empty.");
    }
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }


  @Transactional
  public TrainerProfileResponseDTO getTrainerProfile(String userName) {
    Trainer trainer = findTrainerByUsername(userName);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with username " + userName + " not found.");
    }
    TrainerProfileResponseDTO responseDTO = new TrainerProfileResponseDTO();
    responseDTO.setFirstName(trainer.getUser().getFirstName());
    responseDTO.setLastName(trainer.getUser().getLastName());
    responseDTO.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
    responseDTO.setActive(trainer.getUser().isActive());

    // now setting Trainees List
    List<TraineeProfileResponseDTO> dtos = new ArrayList<>();
    for (Trainee trainee : trainer.getTrainees()) {
      TraineeProfileResponseDTO dto = new TraineeProfileResponseDTO();
      dto.setFirstName(trainee.getUser().getFirstName());
      dto.setLastName(trainee.getUser().getLastName());
      dto.setUserName(trainee.getUser().getUsername());
      dtos.add(dto);
    }
    responseDTO.setTraineeList(dtos);
    return responseDTO;
  }

  @Transactional
  public Trainer updateTrainerProfile(UpdateTrainerProfileRequestDTO requestDTO) {
    Trainer trainer = findTrainerByUsername(requestDTO.getUserName());
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with username " + requestDTO.getUserName() + " not found.");
    }
    trainer.getUser().setFirstName(requestDTO.getFirstName());
    trainer.getUser().setLastName(requestDTO.getLastName());
    trainer.getUser().setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
    trainer.getUser().setActive(requestDTO.isActive());

    TrainingType type = trainingTypeRepository.findByName(requestDTO.getSpecialization());
    if (type == null) {
      type = new TrainingType();
      type.setTrainingTypeName(requestDTO.getSpecialization());
      trainingTypeRepository.save(type);
    }

    trainer.setSpecialization(type);
    trainerRepository.save(trainer);

    return trainer;
  }
}
