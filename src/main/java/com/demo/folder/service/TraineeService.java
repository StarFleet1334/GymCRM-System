package com.demo.folder.service;

import static com.demo.folder.utils.Generator.generatePassword;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.AllTraineeRequestDTO;
import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeProfileRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeTrainersRequestDTO;
import com.demo.folder.entity.dto.response.TraineeResponseProfileDTO;
import com.demo.folder.entity.dto.response.TraineeTrainingResponseDTO;
import com.demo.folder.entity.dto.response.TrainerResponseProfileDTO;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO.TrainerInfoDTO;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import com.demo.folder.repository.TrainingRepository;
import com.demo.folder.utils.Generator;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class TraineeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  @Autowired
  private TrainerService trainerService;
  private final TrainingRepository trainingRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
      TrainingRepository trainingRepository, @Lazy PasswordEncoder passwordEncoder) {
    this.traineeRepository = traineeRepository;
    this.trainerRepository = trainerRepository;
    this.trainingRepository = trainingRepository;
    this.passwordEncoder = passwordEncoder;
    LOGGER.info("TraineeService initialized.");

  }

  public Trainee createTrainee(Trainee trainee) {
    LOGGER.info("Creating new Trainee with username: {}", trainee.getUser().getUsername());
    traineeRepository.save(trainee);
    LOGGER.debug("Trainee created: {}", trainee);
    return trainee;
  }

  @Transactional
  public Trainee findTraineeByUsername(String username) {
    LOGGER.info("Finding Trainee by username: {}", username);
    if (username == null || username.isEmpty()) {
      LOGGER.info("Username cannot be null or empty.");
    }

    Trainee trainee = traineeRepository.findByUsername(username);

    if (trainee == null) {
      LOGGER.info("Trainee with username {} not found.", username);
    }
    return trainee;
  }

  @Transactional(readOnly = true)
  public List<Trainee> getAllTrainees() {
    LOGGER.info("Fetching all Trainees.");
    List<Trainee> trainees = traineeRepository.findAll();
    if (trainees.isEmpty()) {
      throw new EntityNotFoundException("No trainees found.");
    }
    return trainees;
  }


  @Transactional
  public void activateTrainee(Long traineeId) {
    LOGGER.info("Activating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, true);
  }

  @Transactional
  public void deactivateTrainee(Long traineeId) {
    LOGGER.info("Deactivating Trainee with ID: {}", traineeId);
    traineeRepository.updateTraineeStatus(traineeId, false);
  }

  @Transactional
  public void deleteTraineeById(Long traineeId) {
    LOGGER.info("Deleting Trainee with ID: {}", traineeId);
    traineeRepository.deleteById(traineeId);
  }

  @Transactional
  public void updateTrainee(Trainee trainee) {
    String username = trainee.getUser().getUsername();
    LOGGER.info("Updating Trainee with username: {}", trainee.getUser().getUsername());
    Trainee existingTrainee = traineeRepository.findByUsername(username);
    if (existingTrainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }
    traineeRepository.save(trainee);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAssignedTrainers(Trainee trainee) {
    String username = trainee.getUser().getUsername();
    LOGGER.info("Fetching assigned trainers for Trainee: {}", trainee.getUser().getUsername());
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
  public String[] createTrainee(CreateTraineeRequestDTO traineeRequestDTO) {
    if (traineeRequestDTO.getFirstName() == null || traineeRequestDTO.getFirstName().isEmpty() ||
        traineeRequestDTO.getLastName() == null || traineeRequestDTO.getLastName().isEmpty() ) {
      throw new IllegalArgumentException("Trainee's first name or last name must not be empty");
    }
    LOGGER.info("Creating new Trainee with first name: {}", traineeRequestDTO.getFirstName());
    String plainTextPassword = generatePassword();
    String[] arr = new String[2];
    arr[0] = plainTextPassword;
    // Create Trainee and User
    Trainee trainee = new Trainee();
    User user = new User();

    user.setFirstName(traineeRequestDTO.getFirstName());
    user.setLastName(traineeRequestDTO.getLastName());
    user.setUsername(Generator.generateUserName(traineeRequestDTO.getFirstName(),
        traineeRequestDTO.getLastName()));
    user.setPassword(passwordEncoder.encode(plainTextPassword));
    user.setActive(true);

    // Set the user to trainee
    trainee.setUser(user);
    trainee.setDateOfBirth(traineeRequestDTO.getDateOfBirth());
    trainee.setAddress(traineeRequestDTO.getAddress());
    createTrainee(trainee);
    arr[1] = user.getUsername();
    return arr;
  }

  @Transactional
  public UpdateTraineeTrainersResponseDTO updateTraineeTrainersAdd(String traineeUserName,
      UpdateTraineeTrainersRequestDTO requestDTO) {
    Trainee trainee = traineeRepository.findByUsername(traineeUserName);
    if (trainee == null) {
      throw new IllegalArgumentException(
          "Trainee with username " + traineeUserName + " not found.");
    }
    List<Trainer> trainers = new ArrayList<>();
    for (String trainerUsername : requestDTO.getTrainerUsernames()) {
      Trainer trainer = trainerRepository.findByUsername(trainerUsername);
      if (trainer == null) {
        throw new IllegalArgumentException(
            "Trainer with username " + trainerUsername + " not found.");
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

  @Transactional
  public UpdateTraineeTrainersResponseDTO updateTraineeTrainersRemove(String traineeUserName,
      UpdateTraineeTrainersRequestDTO requestDTO) {
    Trainee trainee = traineeRepository.findByUsername(traineeUserName);
    if (trainee == null) {
      throw new IllegalArgumentException(
          "Trainee with username " + traineeUserName + " not found.");
    }
    List<Trainer> trainers = new ArrayList<>();
    for (String trainerUsername : requestDTO.getTrainerUsernames()) {
      Trainer trainer = trainerRepository.findByUsername(trainerUsername);
      if (trainer == null) {
        throw new IllegalArgumentException(
            "Trainer with username " + trainerUsername + " not found.");
      }
      trainers.add(trainer);
      trainee.getTrainers().remove(trainer);
      trainer.getTrainees().remove(trainee);
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
  public Trainee updateTraineeProfile(String traineeUserName,
      UpdateTraineeProfileRequestDTO requestDTO) {

    Trainee trainee = findTraineeByUsername(traineeUserName);
    System.out.println("US: " + trainee.getUser().getUsername());

    trainee.getUser().setFirstName(requestDTO.getFirstName());
    trainee.getUser().setLastName(requestDTO.getLastName());
    trainee.getUser().setUsername(traineeUserName);
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
    LOGGER.info("Deleting Trainee with ID: {}", traineeId);
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

  @Transactional
  public List<AllTraineeRequestDTO> getAllTraineesDetails() {
    List<Trainee> trainees = getAllTrainees();
    List<AllTraineeRequestDTO> dtos = new ArrayList<>();

    for (Trainee trainee : trainees) {
      AllTraineeRequestDTO dto = new AllTraineeRequestDTO();
      dto.setId(trainee.getId());
      dto.setFirstName(trainee.getUser().getFirstName());
      dto.setLastName(trainee.getUser().getLastName());
      dto.setUsername(trainee.getUser().getUsername());
      dto.setPassword(trainee.getUser().getPassword());
      dto.setAddress(trainee.getAddress());
      dto.setDateOfBirth(trainee.getDateOfBirth());
      dto.setActive(trainee.getUser().isActive());

      List<String> trainerUsernames = new ArrayList<>();
      for (Trainer trainer : trainee.getTrainers()) {
        trainerUsernames.add(trainer.getUser().getUsername());
      }
      dto.setTrainers(trainerUsernames);

      List<TraineeTrainingResponseDTO> trainingResponseDTOS = new ArrayList<>();
      for (Training training : trainee.getTrainings()) {
        TraineeTrainingResponseDTO trainingDTO = new TraineeTrainingResponseDTO();
        trainingDTO.setTrainerName(training.getTrainer().getUser().getUsername());
        trainingDTO.setTrainingName(training.getTrainingName());
        trainingDTO.setTrainingType(
            training.getTrainer().getSpecialization().getTrainingTypeName());
        trainingDTO.setTrainingDuration(training.getTrainingDuration());
        trainingDTO.setTrainingDate(training.getTrainingDate());
        trainingResponseDTOS.add(trainingDTO);
      }
      dto.setTrainings(trainingResponseDTOS);

      dtos.add(dto);
    }
    return dtos;
  }


  @Transactional
  public TraineeResponseProfileDTO getTraineeProfileByUsername(String username)
      throws EntityNotFoundException {
    Trainee trainee = findTraineeByUsername(username);
    TraineeResponseProfileDTO traineeResponseProfileDTO = new TraineeResponseProfileDTO();
    traineeResponseProfileDTO.setFirstName(trainee.getUser().getFirstName());
    traineeResponseProfileDTO.setLastName(trainee.getUser().getLastName());
    traineeResponseProfileDTO.setDate_of_birth(trainee.getDateOfBirth());
    traineeResponseProfileDTO.setAddress(trainee.getAddress());
    traineeResponseProfileDTO.setActive(trainee.getUser().isActive());

    List<Training> trainings = trainee.getTrainings();
    if (!trainings.isEmpty()) {
      List<TraineeTrainingResponseDTO> traineeTrainingResponseDTOS = new ArrayList<>();
      for (Training training : trainings) {
        TraineeTrainingResponseDTO traineeTrainingResponseDTO = new TraineeTrainingResponseDTO();
        traineeTrainingResponseDTO.setTrainerName(training.getTrainer().getUser().getUsername());
        traineeTrainingResponseDTO.setTrainingName(training.getTrainingName());
        traineeTrainingResponseDTO.setTrainingType(
            training.getTrainer().getSpecialization().getTrainingTypeName());
        traineeTrainingResponseDTO.setTrainingDuration(training.getTrainingDuration());
        traineeTrainingResponseDTO.setTrainingDate(training.getTrainingDate());
        traineeTrainingResponseDTOS.add(traineeTrainingResponseDTO);
      }
      traineeResponseProfileDTO.setTrainings(traineeTrainingResponseDTOS);
    }

    List<TrainerResponseProfileDTO> trainerResponseProfileDTOList = getTrainerProfiles(trainee);
    traineeResponseProfileDTO.setTrainerProfileList(trainerResponseProfileDTOList);
    return traineeResponseProfileDTO;
  }

  private static List<TrainerResponseProfileDTO> getTrainerProfiles(Trainee trainee) {
    List<TrainerResponseProfileDTO> trainerResponseProfileDTOList = new ArrayList<>();
    for (Trainer trainer : trainee.getTrainers()) {
      TrainerResponseProfileDTO trainerResponseProfileDTO = new TrainerResponseProfileDTO();
      trainerResponseProfileDTO.setFirstName(trainer.getUser().getFirstName());
      trainerResponseProfileDTO.setLastName(trainer.getUser().getLastName());
      trainerResponseProfileDTO.setUserName(trainer.getUser().getUsername());
      trainerResponseProfileDTO.setTrainingType(trainer.getSpecialization().getTrainingTypeName());
      trainerResponseProfileDTOList.add(trainerResponseProfileDTO);
    }
    return trainerResponseProfileDTOList;
  }


  @Transactional
  public AllTraineeRequestDTO updateTrainee(String username,
      UpdateTraineeProfileRequestDTO requestDTO) throws EntityNotFoundException {

    Trainee updateTraineeProfile = updateTraineeProfile(username, requestDTO);

    AllTraineeRequestDTO allTraineeRequestDTO = new AllTraineeRequestDTO();
    allTraineeRequestDTO.setFirstName(updateTraineeProfile.getUser().getFirstName());
    allTraineeRequestDTO.setLastName(updateTraineeProfile.getUser().getLastName());
    allTraineeRequestDTO.setUsername(updateTraineeProfile.getUser().getUsername());
    allTraineeRequestDTO.setDateOfBirth(updateTraineeProfile.getDateOfBirth());
    allTraineeRequestDTO.setAddress(updateTraineeProfile.getAddress());
    allTraineeRequestDTO.setActive(updateTraineeProfile.getUser().isActive());

    List<String> trainersUserNames = new ArrayList<>();
    for (Trainer trainer : updateTraineeProfile.getTrainers()) {
      trainersUserNames.add(trainer.getUser().getUsername());
    }
    allTraineeRequestDTO.setTrainers(trainersUserNames);
    return allTraineeRequestDTO;
  }


  public Date addDurationToTrainingDate(Date trainingDate, Number trainingDurationInMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.MINUTE, trainingDurationInMinutes.intValue());
    return calendar.getTime();
  }

  @Transactional
  public List<TraineeTrainingResponseDTO> getFilteredTrainings(String username, Date periodFrom,
      Date periodTo, String trainingName, String trainingType) throws EntityNotFoundException {
    Trainee trainee = findTraineeByUsername(username);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with username " + username + " not found.");
    }

    return trainee.getTrainings().stream()
        .filter(training -> periodFrom == null || !training.getTrainingDate().before(periodFrom))
        .filter(training -> {
          Date calculatedPeriodTo = addDurationToTrainingDate(training.getTrainingDate(),
              training.getTrainingDuration());
          return periodTo == null || !calculatedPeriodTo.after(periodTo);
        })
        .filter(training -> trainingName == null || training.getTrainingName()
            .equalsIgnoreCase(trainingName))
        .filter(training -> trainingType == null || training.getTrainer().getSpecialization()
            .getTrainingTypeName().equalsIgnoreCase(trainingType))
        .map(this::mapToTraineeTrainingResponseDTO)
        .collect(Collectors.toList());
  }

  private TraineeTrainingResponseDTO mapToTraineeTrainingResponseDTO(Training training) {
    TraineeTrainingResponseDTO dto = new TraineeTrainingResponseDTO();
    dto.setTrainerName(training.getTrainer().getUser().getUsername());
    dto.setTrainingName(training.getTrainingName());
    dto.setTrainingType(training.getTrainer().getSpecialization().getTrainingTypeName());
    dto.setTrainingDuration(training.getTrainingDuration());
    dto.setTrainingDate(training.getTrainingDate());
    return dto;
  }


  @Transactional
  public void activateTraineeRest(String username) {
    Trainee trainee = findTraineeByUsername(username);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with given username not found");
    }

    if (trainee.getUser().isActive()) {
      throw new IllegalArgumentException("Trainee is already activated");
    }

    activateTrainee(trainee.getId());
  }

  @Transactional
  public void deActivateTraineeRest(String username) {
    Trainee trainee = findTraineeByUsername(username);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with given username not found");
    }

    if (!trainee.getUser().isActive()) {
      throw new IllegalArgumentException("Trainee is already de-activated");
    }

    deactivateTrainee(trainee.getId());
  }
}
