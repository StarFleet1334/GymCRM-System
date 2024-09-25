package com.demo.folder.controller;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.dto.request.TraineeRequestDTO;
import com.demo.folder.entity.dto.response.TraineeTrainingResponseDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeProfileRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeTrainersRequestDTO;
import com.demo.folder.entity.dto.response.TraineeResponseProfileDTO;
import com.demo.folder.entity.dto.response.TraineeResponse;
import com.demo.folder.entity.dto.response.TrainerResponseProfileDTO;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/trainee", produces = {"application/JSON", "application/XML"})
@Tag(name = "Trainee Controller", description = "Operations related to trainees")

public class TraineeController {

  @Autowired
  private TraineeService traineeService;

  @PostMapping("/register")
  @Operation(summary = "Register Trainee", description = "Registers a new trainee.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered trainee"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> registerTrainee(@Valid @RequestBody TraineeRequestDTO traineeRequestDTO,
      BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      Trainee registeredTrainee = traineeService.createTrainee(traineeRequestDTO);
      TraineeResponse response = new TraineeResponse();
      response.setUsername(registeredTrainee.getUser().getUsername());
      response.setPassword(registeredTrainee.getUser().getPassword());

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while registering the trainee.");
    }
  }

  @GetMapping("/all")
  @Operation(summary = "Get All Trainees", description = "Retrieves a list of all trainees.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainees"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TraineeRequestDTO>> getAllTrainee() {
    List<Trainee> trainees = traineeService.getAllTrainees();
    System.out.println("Trainees: " + trainees);
    List<TraineeRequestDTO> traineeRequestDTOS = new ArrayList<>();
    for (Trainee trainee : trainees) {
      TraineeRequestDTO traineeRequestDTO = new TraineeRequestDTO();
      traineeRequestDTO.setId(trainee.getId());
      traineeRequestDTO.setFirstName(trainee.getUser().getFirstName());
      traineeRequestDTO.setLastName(trainee.getUser().getLastName());
      traineeRequestDTO.setUsername(trainee.getUser().getUsername());
      traineeRequestDTO.setPassword(trainee.getUser().getPassword());
      traineeRequestDTO.setAddress(trainee.getAddress());
      traineeRequestDTO.setDateOfBirth(trainee.getDateOfBirth());
      traineeRequestDTO.setActive(trainee.getUser().isActive());

      // Setting trainee's trainer's usernames to list
      List<String> trainersUserNames = new ArrayList<>();
      for (Trainer trainer : trainee.getTrainers()) {
        trainersUserNames.add(trainer.getUser().getUsername());
      }
      traineeRequestDTO.setTrainers(trainersUserNames);

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
        traineeRequestDTO.setTrainings(traineeTrainingResponseDTOS);
      }

      traineeRequestDTOS.add(traineeRequestDTO);
    }

    return ResponseEntity.ok(traineeRequestDTOS);
  }

  @PatchMapping("/activate")
  @Operation(summary = "Activate Trainee", description = "Activates a trainee's account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully activated trainee"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> activateTrainee(
      @RequestParam(name = "username") String username,
      @RequestParam(name = "isActive") boolean isActive
  ) {
    Trainee trainee = traineeService.findTraineeByUsername(username);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with given username not found");
    } else {
      if (trainee.getUser().isActive()) {
        throw new IllegalArgumentException("Trainee is already activated");
      } else {
        if (!isActive) {
          throw new IllegalArgumentException(
              "Providing false as isActive is wrong during activation");
        } else {
          traineeService.activateTrainee(trainee.getId());
          return ResponseEntity.ok("Trainee activated");
        }
      }
    }
  }

  @PatchMapping("/de-activate")
  @Operation(summary = "Deactivate Trainee", description = "Deactivates a trainee's account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deactivated trainee"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> deactivateTrainee(
      @RequestParam(name = "username") String username,
      @RequestParam(name = "isActive") boolean isActive
  ) {
    Trainee trainee = traineeService.findTraineeByUsername(username);
    if (trainee == null) {
      throw new EntityNotFoundException("Trainee with given username not found");
    } else {
      if (!trainee.getUser().isActive()) {
        throw new IllegalArgumentException("Trainee is already de-activated");
      } else {
        if (isActive) {
          throw new IllegalArgumentException(
              "Providing true as isActive is wrong during de-activation");
        } else {
          traineeService.deactivateTrainee(trainee.getId());
          return ResponseEntity.ok("Trainee deactivated");
        }
      }
    }
  }

  @DeleteMapping("/delete")
  @Operation(summary = "Delete Trainee", description = "Deletes a trainee from the system.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted trainee"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> deleteTrainee(@RequestParam(name = "username") String username) {
    try {
      Trainee trainee = traineeService.findTraineeByUsername(username);
      if (trainee == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Trainee with given username not found");
      }
      traineeService.deleteTraineeByIdVolTwo(trainee.getId());
      return ResponseEntity.ok("Trainee deleted");
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while deleting the trainee.");
    }
  }

  @GetMapping("/profile")
  @Operation(summary = "Get Trainee Profile", description = "Retrieves a trainee's profile by username.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee profile"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> getTraineeProfile(@RequestParam(name = "username") String username) {
    try {
      Trainee trainee = traineeService.findTraineeByUsername(username);
      TraineeResponseProfileDTO traineeResponseProfileDTO = new TraineeResponseProfileDTO();
      traineeResponseProfileDTO.setFirstName(trainee.getUser().getFirstName());
      traineeResponseProfileDTO.setLastName(trainee.getUser().getLastName());
      traineeResponseProfileDTO.setDate_of_birth(trainee.getDateOfBirth());
      traineeResponseProfileDTO.setAddress(trainee.getAddress());
      traineeResponseProfileDTO.setActive(trainee.getUser().isActive());

      // Check if trainee has trainings before accessing it
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

      return ResponseEntity.ok(traineeResponseProfileDTO);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while fetching the trainee profile.");
    }
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


  @PutMapping("/update-trainers")
  @Operation(summary = "Update Trainee's Trainers", description = "Updates the list of trainers assigned to a trainee.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated trainee's trainers"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainee or Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> updateTraineeTrainers(
      @Valid @RequestBody UpdateTraineeTrainersRequestDTO requestDTO, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      UpdateTraineeTrainersResponseDTO responseDTO = traineeService.updateTraineeTrainers(
          requestDTO);
      return ResponseEntity.ok(responseDTO);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while updating the trainee trainers.");
    }
  }

  @PutMapping("/update-trainee")
  @Operation(summary = "Update Trainee Profile", description = "Updates a trainee's profile information.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated trainee"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> updateTrainee(
      @Valid @RequestBody UpdateTraineeProfileRequestDTO requestDTO, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      Trainee updateTraineeProfile = traineeService.updateTraineeProfile(requestDTO);
      TraineeRequestDTO traineeRequestDTO = new TraineeRequestDTO();
      traineeRequestDTO.setFirstName(updateTraineeProfile.getUser().getFirstName());
      traineeRequestDTO.setLastName(updateTraineeProfile.getUser().getLastName());
      traineeRequestDTO.setUsername(updateTraineeProfile.getUser().getUsername());
      traineeRequestDTO.setDateOfBirth(updateTraineeProfile.getDateOfBirth());
      traineeRequestDTO.setAddress(updateTraineeProfile.getAddress());
      traineeRequestDTO.setActive(updateTraineeProfile.getUser().isActive());

      List<String> trainersUserNames = new ArrayList<>();
      for (Trainer trainer : updateTraineeProfile.getTrainers()) {
        trainersUserNames.add(trainer.getUser().getUsername());
      }
      traineeRequestDTO.setTrainers(trainersUserNames);

      return ResponseEntity.ok(traineeRequestDTO);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while updating the trainee profile.");
    }
  }

  @GetMapping("/{username}/unassigned-trainers")
  @Operation(summary = "Get Unassigned Trainers", description = "Retrieves trainers not assigned to the trainee.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved unassigned trainers"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> getUnassignedTrainers(@PathVariable String username) {
    try {
      UpdateTraineeTrainersResponseDTO unassignedTrainers = traineeService.getUnassignedActiveTrainers(
          username);
      return ResponseEntity.ok(unassignedTrainers);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while fetching unassigned trainers.");
    }
  }

  @GetMapping("/trainings")
  @Operation(summary = "Get Trainee Trainings", description = "Retrieves trainings assigned to the trainee.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee trainings"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainee not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> getTrainings(
      @RequestParam(name = "userName") String userName,
      @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
      @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
      @RequestParam(name = "trainingName", required = false) String trainingName,
      @RequestParam(name = "trainingType", required = false) String trainingType
  ) {
    try {
      Trainee trainee = traineeService.findTraineeByUsername(userName);
      if (trainee == null) {
        throw new EntityNotFoundException("Trainee with username " + userName + " not found.");
      }

      List<Training> trainings = trainee.getTrainings();

      List<Training> filteredTrainings = trainings.stream()
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
          .toList();

      List<TraineeTrainingResponseDTO> responseDTOList = filteredTrainings.stream()
          .map(training -> {
            TraineeTrainingResponseDTO traineeTrainingResponseDTO = new TraineeTrainingResponseDTO();
            traineeTrainingResponseDTO.setTrainerName(
                training.getTrainer().getUser().getUsername());
            traineeTrainingResponseDTO.setTrainingName(training.getTrainingName());
            traineeTrainingResponseDTO.setTrainingType(
                training.getTrainer().getSpecialization().getTrainingTypeName());
            traineeTrainingResponseDTO.setTrainingDuration(training.getTrainingDuration());
            traineeTrainingResponseDTO.setTrainingDate(training.getTrainingDate());
            return traineeTrainingResponseDTO;
          }).toList();

      return ResponseEntity.ok(responseDTOList);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while fetching the trainee trainings.");
    }
  }

  public Date addDurationToTrainingDate(Date trainingDate, Number trainingDurationInMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.MINUTE, trainingDurationInMinutes.intValue());
    return calendar.getTime();
  }


}
