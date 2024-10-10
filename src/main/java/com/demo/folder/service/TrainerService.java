package com.demo.folder.service;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTrainerProfileRequestDTO;
import com.demo.folder.entity.dto.response.TraineeProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerTrainingResponseDTO;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingTypeRepository;
import com.demo.folder.utils.FileUtil;
import com.demo.folder.utils.Generator;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public TrainerService(TrainerRepository trainerRepository,
      TrainingTypeRepository trainingTypeRepository, @Lazy PasswordEncoder passwordEncoder) {
    this.trainerRepository = trainerRepository;
    this.trainingTypeRepository = trainingTypeRepository;
    this.passwordEncoder = passwordEncoder;
  }


  @Transactional
  public Trainer createTrainer(Trainer trainer) {
    LOGGER.info("Creating new Trainer with username: {}", trainer.getUser().getUsername());
    trainerRepository.save(trainer);
    LOGGER.debug("Trainer created: {}", trainer);
    return trainer;
  }

  @Transactional
  public String[] createTrainer(TrainerRequestDTO trainerRequestDTO) {
    if (trainerRequestDTO.getFirstName() == null || trainerRequestDTO.getFirstName().isEmpty() ||
        trainerRequestDTO.getLastName() == null || trainerRequestDTO.getLastName().isEmpty()) {
      throw new IllegalArgumentException("Trainer's first name or last name must not be empty");
    }
    LOGGER.info("Creating new Trainer with first name: {}", trainerRequestDTO.getFirstName());
    Optional<TrainingType> existingTrainingType = trainingTypeRepository.findById(
        trainerRequestDTO.getTrainingTypeId());
    String[] arr = new String[2];
    String plainTextPassword = Generator.generatePassword();
    arr[0] = plainTextPassword;

    TrainingType trainingType;
    if (existingTrainingType.isPresent()) {
      trainingType = existingTrainingType.get();
    } else {
      return null;
    }
    User user = createUser(trainerRequestDTO,plainTextPassword);

    Trainer trainer = new Trainer();
    trainer.setSpecialization(trainingType);
    trainer.setUser(user);

    trainerRepository.save(trainer);

    arr[1] = user.getUsername();

    FileUtil.writeCredentialsToFile("trainer_credentials.txt", arr[1],
        arr[0]);

    return arr;
  }


  @Transactional(readOnly = true)
  public Trainer findTrainerByUsername(String username) {
    LOGGER.info("Finding Trainer by username: {}", username);
    if (username == null || username.isEmpty()) {
      LOGGER.info("Username cannot be null or empty.");
    }

    Trainer trainer = trainerRepository.findByUsername(username);
    if (trainer == null) {
      LOGGER.info("Trainer with username {} not found.", username);
    }
    return trainer;
  }

  @Transactional
  public void activateTrainer(Long trainerId) {
    LOGGER.info("Activating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, true);
  }

  @Transactional
  public void deactivateTrainer(Long trainerId) {
    LOGGER.info("Deactivating Trainer with ID: {}", trainerId);
    trainerRepository.updateTrainerStatus(trainerId, false);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAllTrainers() {
    LOGGER.info("Fetching all Trainers.");
    List<Trainer> trainers = trainerRepository.findAll();

    if (trainers.isEmpty()) {
      throw new EntityNotFoundException("No trainers found.");
    }
    return trainers;
  }

  @Transactional
  public void updateTrainer(Trainer trainer) {
    String username = trainer.getUser().getUsername();
    LOGGER.info("Updating Trainer with username: {}", trainer.getUser().getUsername());
    Trainer existingTrainer = trainerRepository.findByUsername(username);
    if (existingTrainer == null) {
      throw new EntityNotFoundException("Trainer with username " + username + " not found.");
    }
    trainerRepository.save(trainer);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(List<Trainer> assignedTrainers) {
    LOGGER.info("Fetching unassigned Trainers.");
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
    List<TraineeProfileResponseDTO> dtos = trainer.getTrainees().stream()
        .map(trainee -> {
          TraineeProfileResponseDTO dto = new TraineeProfileResponseDTO();
          dto.setFirstName(trainee.getUser().getFirstName());
          dto.setLastName(trainee.getUser().getLastName());
          dto.setUserName(trainee.getUser().getUsername());
          return dto;
        })
        .collect(Collectors.toList());
    responseDTO.setTraineeList(dtos);
    return responseDTO;
  }

  @Transactional
  public Trainer updateTrainerProfile(String trainerUserName,
      UpdateTrainerProfileRequestDTO requestDTO) {
    Trainer trainer = findTrainerByUsername(trainerUserName);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with username " + trainerUserName + " not found.");
    }
    trainer.getUser().setFirstName(requestDTO.getFirstName());
    trainer.getUser().setLastName(requestDTO.getLastName());
    trainer.getUser().setUsername(
        Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
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


  @Transactional
  public List<TrainerRequestDTO> retrieveAllTrainers() {
    List<Trainer> trainers = getAllTrainers();
    List<TrainerRequestDTO> trainerRequestDTOS = new ArrayList<>();
    for (Trainer trainer : trainers) {
      TrainingTypeRequestDTO trainingTypeRequestDTO = new TrainingTypeRequestDTO();
      trainingTypeRequestDTO.setId(trainer.getSpecialization().getId());
      trainingTypeRequestDTO.setTrainingTypeName(trainer.getSpecialization().getTrainingTypeName());

      TrainerRequestDTO trainerRequestDTO = new TrainerRequestDTO();
      trainerRequestDTO.setId(trainer.getId());
      trainerRequestDTO.setFirstName(trainer.getUser().getFirstName());
      trainerRequestDTO.setLastName(trainer.getUser().getLastName());
      trainerRequestDTO.setUsername(trainer.getUser().getUsername());
      trainerRequestDTO.setPassword(trainer.getUser().getPassword());
      trainerRequestDTO.setTrainingTypeId(trainingTypeRequestDTO.getId());
      trainerRequestDTO.setActive(trainer.getUser().isActive());

      // setting trainer's trainee's
      List<String> traineesUserNames = new ArrayList<>();
      for (Trainee trainee : trainer.getTrainees()) {
        traineesUserNames.add(trainee.getUser().getUsername());
      }
      trainerRequestDTO.setTrainees(traineesUserNames);

      trainerRequestDTOS.add(trainerRequestDTO);
    }
    return trainerRequestDTOS;
  }


  @Transactional
  public TrainerProfileResponseDTO updateTrainer(String username,
      UpdateTrainerProfileRequestDTO requestDTO) throws EntityNotFoundException {
    Trainer trainer = updateTrainerProfile(username, requestDTO);
    TrainerProfileResponseDTO dto = new TrainerProfileResponseDTO();
    dto.setFirstName(trainer.getUser().getFirstName());
    dto.setLastName(trainer.getUser().getLastName());
    dto.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
    dto.setActive(trainer.getUser().isActive());

    // Now trainee's list
    List<TraineeProfileResponseDTO> traineeProfileResponseDTOS = trainer.getTrainees().stream()
        .map(traineeProfile -> {
          TraineeProfileResponseDTO traineeProfileResponseDTO = new TraineeProfileResponseDTO();
          traineeProfileResponseDTO.setFirstName(traineeProfile.getUser().getFirstName());
          traineeProfileResponseDTO.setLastName(traineeProfile.getUser().getLastName());
          traineeProfileResponseDTO.setUserName(traineeProfile.getUser().getUsername());
          return traineeProfileResponseDTO;
        })
        .collect(Collectors.toList());

    dto.setTraineeList(traineeProfileResponseDTOS);

    return dto;
  }

  @Transactional
  public List<TrainerTrainingResponseDTO> getFilteredTrainingsForTrainer(String username,
      Date periodFrom, Date periodTo, String traineeName) throws EntityNotFoundException {
    Trainer trainer = findTrainerByUsername(username);
    if (trainer == null) {
      throw new com.demo.folder.error.exception.EntityNotFoundException(
          "Trainer with username " + username + " not found.");
    }

    List<Training> trainings = trainer.getTrainings();

    List<Training> filteredTrainings = trainings.stream()
        .filter(training -> periodFrom == null || !training.getTrainingDate().before(periodFrom))
        .filter(training -> {
          Date calculatedPeriodTo = addDurationToTrainingDate(training.getTrainingDate(),
              training.getTrainingDuration());
          return periodTo == null || !calculatedPeriodTo.after(periodTo);
        })
        .filter(training -> traineeName == null || training.getTrainee().getUser().getUsername()
            .equalsIgnoreCase(traineeName))
        .toList();

    List<TrainerTrainingResponseDTO> responseDTOList = filteredTrainings.stream()
        .map(training -> {
          TrainerTrainingResponseDTO traineeTrainingDTO = new TrainerTrainingResponseDTO();
          traineeTrainingDTO.setTraineeName(training.getTrainer().getUser().getUsername());
          traineeTrainingDTO.setTrainingName(training.getTrainingName());
          traineeTrainingDTO.setTrainingType(
              training.getTrainer().getSpecialization().getTrainingTypeName());
          traineeTrainingDTO.setTrainingDuration(training.getTrainingDuration());
          traineeTrainingDTO.setTrainingDate(training.getTrainingDate());
          return traineeTrainingDTO;
        }).toList();

    return responseDTOList;
  }

  @Transactional
  public void modifyTrainerState(String username,boolean state) {
    Trainer trainer = findTrainerByUsername(username);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with given username not found");
    }

    if (trainer.getUser().isActive() && state) {
      throw new IllegalArgumentException("Trainer is already activated");
    }

    if (!trainer.getUser().isActive() && !state) {
      throw new IllegalArgumentException("Trainer is already activated");
    }
    if (state) {
      activateTrainer(trainer.getId());
    }
    deactivateTrainer(trainer.getId());
  }

  private User createUser(TrainerRequestDTO requestDTO, String plainTextPassword) {
    User user = new User();
    user.setFirstName(requestDTO.getFirstName());
    user.setLastName(requestDTO.getLastName());
    user.setUsername(Generator.generateUserName(requestDTO.getFirstName(), requestDTO.getLastName()));
    user.setPassword(passwordEncoder.encode(plainTextPassword));
    user.setActive(true);
    return user;
  }

  private Date addDurationToTrainingDate(Date trainingDate, Number trainingDurationInMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.MINUTE, trainingDurationInMinutes.intValue());
    return calendar.getTime();
  }


}
