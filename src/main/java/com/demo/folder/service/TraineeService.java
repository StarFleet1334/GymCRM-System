package com.demo.folder.service;

import static com.demo.folder.utils.Generator.generatePassword;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.TraineeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeProfileRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeTrainersRequestDTO;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO.TrainerInfoDTO;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingRepository;
import com.demo.folder.utils.Generator;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TraineeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  @Autowired
  private TrainerService trainerService;
  private final TrainingRepository trainingRepository;

  @Autowired
  public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
      TrainingRepository trainingRepository) {
    this.traineeRepository = traineeRepository;
    this.trainerRepository = trainerRepository;
    this.trainingRepository = trainingRepository;
//    LOGGER.info("TraineeService initialized.");

  }

  public Trainee createTrainee(Trainee trainee) {
//    LOGGER.info("Creating new Trainee with username: {}", trainee.getUser().getUsername());
    traineeRepository.save(trainee);
//    LOGGER.debug("Trainee created: {}", trainee);

    return trainee;
  }

  @Transactional
  public Trainee findTraineeByUsername(String username) {
//    LOGGER.info("Finding Trainee by username: {}", username);
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }

    Trainee trainee = traineeRepository.findByUsername(username);

    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    return trainee;
  }

  @Transactional(readOnly = true)
  public List<Trainee> getAllTrainees() {
//    LOGGER.info("Fetching all Trainees.");
    List<Trainee> trainees = traineeRepository.findAll();
    if (trainees.isEmpty()) {
      throw new EntityNotFoundException("No trainees found.");
    }
    return trainees;
  }


  @Transactional
  public void activateTrainee(Long traineeId) {
//    LOGGER.info("Activating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, true);
  }

  @Transactional
  public void deactivateTrainee(Long traineeId) {
//    LOGGER.info("Deactivating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, false);
  }

  @Transactional
  public void deleteTraineeById(Long traineeId) {
//    LOGGER.info("Deleting Trainee with ID: {}", traineeId);
    traineeRepository.deleteById(traineeId);
  }

  @Transactional
  public void updateTrainee(Trainee trainee) {
    String username = trainee.getUser().getUsername();
//    LOGGER.info("Updating Trainee with username: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    traineeRepository.save(trainee);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAssignedTrainers(Trainee trainee) {
    String username = trainee.getUser().getUsername();
//    LOGGER.info("Fetching assigned trainers for Trainee: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    return trainee.getTrainers();
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(Trainee trainee) {
    String username = trainee.getUser().getUsername();
    LOGGER.info("Fetching unassigned trainers for Trainee: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    List<Trainer> assignedTrainers = trainee.getTrainers();
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }


  @Transactional
  public Trainee createTrainee(TraineeRequestDTO traineeRequestDTO) {
//    LOGGER.info("Creating new Trainee with first name: {}", traineeDTO.getFirstName());

    // Create Trainee and User
    Trainee trainee = new Trainee();
    User user = new User();

    user.setFirstName(traineeRequestDTO.getFirstName());
    user.setLastName(traineeRequestDTO.getLastName());
    user.setUsername(Generator.generateUserName(traineeRequestDTO.getFirstName(), traineeRequestDTO.getLastName()));
    user.setPassword(generatePassword());

    // Set the user to trainee
    trainee.setUser(user);
    trainee.setDateOfBirth(traineeRequestDTO.getDateOfBirth());
    trainee.setAddress(traineeRequestDTO.getAddress());
    createTrainee(trainee);
    return trainee;
  }

  @Transactional
  public UpdateTraineeTrainersResponseDTO updateTraineeTrainers(UpdateTraineeTrainersRequestDTO requestDTO) {
    Trainee trainee = traineeRepository.findByUsername(requestDTO.getTraineeUsername());
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee with username " + requestDTO.getTraineeUsername() + " not found.");
    }
    List<Trainer> trainers = new ArrayList<>();
    for (String trainerUsername : requestDTO.getTrainerUsernames()) {
      Trainer trainer = trainerRepository.findByUsername(trainerUsername);
      if (trainer == null) {
        throw new IllegalArgumentException("Trainer with username " + trainerUsername + " not found.");
      }
      trainers.add(trainer);
      trainee.getTrainers().add(trainer);
      trainer.getTrainees().add(trainee);
      updateTrainee(trainee);
      trainerService.updateTrainer(trainer);
    }

    return getUpdateTraineeTrainersResponseDTO(
        trainers);

  }

  private static UpdateTraineeTrainersResponseDTO getUpdateTraineeTrainersResponseDTO(
      List<Trainer> trainers) {
    return null;
  }

  @Transactional
  public Trainee updateTraineeProfile(UpdateTraineeProfileRequestDTO requestDTO) {

      Trainee trainee = findTraineeByUsername(requestDTO.getUsername());
    System.out.println("US: " + trainee.getUser().getUsername());
      if (trainee == null) {
        throw new IllegalArgumentException("Trainee with username " + requestDTO.getUsername() + " not found.");
      }

      trainee.getUser().setFirstName(requestDTO.getFirstName());
      trainee.getUser().setLastName(requestDTO.getLastName());
      trainee.getUser().setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
      if (requestDTO.getDateOfBirth() != null) {
        trainee.setDateOfBirth(requestDTO.getDateOfBirth());
      }
      if (requestDTO.getAddress() != null) {
        trainee.setAddress(requestDTO.getAddress());
      }
      trainee.getUser().setActive(requestDTO.getIsActive());
      updateTrainee(trainee);
      return trainee;

  }

  @Transactional
  public UpdateTraineeTrainersResponseDTO getUnassignedActiveTrainers(String username) {
    Trainee trainee = traineeRepository.findByUsername(username);
    if (trainee == null) {
      throw new IllegalArgumentException("Trainee with username " + username + " not found.");
    }

    List<Trainer> allTrainers = trainerService.getAllTrainers();

    List<Trainer> assignedTrainers = trainee.getTrainers();

    List<Trainer> unassignedActiveTrainers = allTrainers.stream()
        .filter(trainer -> !assignedTrainers.contains(trainer))
        .filter(x -> x.getUser().isActive())
        .toList();

    List<TrainerInfoDTO> trainerInfoDTOS = new ArrayList<>();
    for (Trainer trainer : unassignedActiveTrainers) {
      TrainerInfoDTO dto = new TrainerInfoDTO();
      dto.setFirstName(trainer.getUser().getFirstName());
      dto.setLastName(trainer.getUser().getLastName());
      dto.setUsername(trainer.getUser().getUsername());
      dto.setTrainerSpecializationId(trainer.getSpecialization().getId());
      trainerInfoDTOS.add(dto);
    }
    UpdateTraineeTrainersResponseDTO responseDTO = new UpdateTraineeTrainersResponseDTO();
    responseDTO.setTrainers(trainerInfoDTOS);
    return responseDTO;

  }

  @Transactional
  public void deleteTraineeByIdVolTwo(Long traineeId) {
//    LOGGER.info("Deleting Trainee with ID: {}", traineeId);
    Trainee trainee = traineeRepository.getCurrentSession().get(Trainee.class, traineeId);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with ID " + traineeId + " not found");
    }
    List<Training> trainings = trainingRepository.findByTrainee(trainee);
    for (Training training : trainings) {
      trainingRepository.delete(training);
    }

    for (Trainer trainer : trainee.getTrainers()) {
      trainer.getTrainees().remove(trainee);
      trainerRepository.save(trainer);
    }
    trainee.getTrainers().clear();
    traineeRepository.deleteById(traineeId);
  }


}
