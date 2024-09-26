package com.demo.folder.controller;


import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.response.TrainerTrainingResponseDTO;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTrainerProfileRequestDTO;
import com.demo.folder.entity.dto.response.TraineeProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerResponseDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/trainer", produces = {"application/JSON", "application/XML"})
@Tag(name = "Trainer Controller", description = "Operations related to trainers")
public class TrainerController {

  @Autowired
  private TrainerService trainerService;


  @PostMapping("/register")
  @Operation(summary = "Register Trainer", description = "Registers a new trainer.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered trainer"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> registerTrainer(@Valid @RequestBody TrainerRequestDTO trainerRequestDTO,
      BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
    }
    Trainer registeredTrainer = trainerService.createTrainer(trainerRequestDTO);
    if (registeredTrainer == null) {
      throw new IllegalArgumentException(
          "Training type credential was incorrect or some other fields were indicated incorrectly.");
    }
    TrainerResponseDTO response = new TrainerResponseDTO();
    response.setUsername(registeredTrainer.getUser().getUsername());
    response.setPassword(registeredTrainer.getUser().getPassword());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/all")
  @Operation(summary = "Get All Trainers", description = "Retrieves all trainers.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainers"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TrainerRequestDTO>> getAllTrainee() {
    List<Trainer> trainers = trainerService.getAllTrainers();
    System.out.println("Trainers: " + trainers);
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
    return ResponseEntity.ok(trainerRequestDTOS);
  }

  @PatchMapping("/activate")
  @Operation(summary = "Activate Trainer", description = "Activates a trainer account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Trainer activated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "404", description = "Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> activateTrainer(
      @RequestParam(name = "username") String username,
      @RequestParam(name = "isActive") boolean isActive
  ) {
    Trainer trainer = trainerService.findTrainerByUsername(username);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with given username not found");
    } else {
      if (trainer.getUser().isActive()) {
        throw new IllegalArgumentException("Trainer is already activated");
      } else {
        if (!isActive) {
          throw new IllegalArgumentException(
              "Providing false as isActive is wrong during activation");
        } else {
          trainerService.activateTrainer(trainer.getId());
          return ResponseEntity.ok("Trainer activated");
        }
      }
    }
  }

  @PatchMapping("/de-activate")
  @Operation(summary = "Deactivate Trainer", description = "Deactivates a trainer account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Trainer deactivated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "404", description = "Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> deactivateTrainer(
      @RequestParam(name = "username") String username,
      @RequestParam(name = "isActive") boolean isActive
  ) {
    Trainer trainer = trainerService.findTrainerByUsername(username);
    if (trainer == null) {
      throw new EntityNotFoundException("Trainer with given username not found");
    } else {
      if (!trainer.getUser().isActive()) {
        throw new IllegalArgumentException("Trainer is already de-activated");
      } else {
        if (isActive) {
          throw new IllegalArgumentException(
              "Providing true as isActive is wrong during de-activation");
        } else {
          trainerService.deactivateTrainer(trainer.getId());
          return ResponseEntity.ok("Trainer deactivated");
        }
      }
    }
  }

  @GetMapping("/trainer-profile")
  @Operation(summary = "Get Trainer Profile", description = "Retrieves a trainer's profile by username.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer profile"),
      @ApiResponse(responseCode = "404", description = "Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<TrainerProfileResponseDTO> getTrainerProfile(
      @Valid @RequestParam(name = "userName") String userName
  ) {
    TrainerProfileResponseDTO dto = trainerService.getTrainerProfile(userName);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/update-trainer")
  @Operation(summary = "Update Trainer Profile", description = "Updates a trainer's profile.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated trainer"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(
      @Valid @RequestBody UpdateTrainerProfileRequestDTO requestDTO
  ) {
    Trainer trainer = trainerService.updateTrainerProfile(requestDTO);
    TrainerProfileResponseDTO dto = new TrainerProfileResponseDTO();
    dto.setFirstName(trainer.getUser().getFirstName());
    dto.setLastName(trainer.getUser().getLastName());
    dto.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
    dto.setActive(trainer.getUser().isActive());

    // Now trainee's list
    List<TraineeProfileResponseDTO> traineeProfileResponseDTOS = new ArrayList<>();
    for (Trainee traineeProfile : trainer.getTrainees()) {
      TraineeProfileResponseDTO traineeProfileResponseDTO = new TraineeProfileResponseDTO();
      traineeProfileResponseDTO.setFirstName(traineeProfile.getUser().getFirstName());
      traineeProfileResponseDTO.setLastName(traineeProfile.getUser().getLastName());
      traineeProfileResponseDTO.setUserName(traineeProfile.getUser().getUsername());
      traineeProfileResponseDTOS.add(traineeProfileResponseDTO);
    }

    dto.setTraineeList(traineeProfileResponseDTOS);

    return ResponseEntity.ok(dto);
  }

  @GetMapping("/trainings")
  @Operation(summary = "Get Trainer's Trainings", description = "Retrieves trainings associated with a trainer.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer's trainings"),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
      @ApiResponse(responseCode = "404", description = "Trainer not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> getTrainings(
      @RequestParam(name = "userName") String userName,
      @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
      @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
      @RequestParam(name = "traineeName", required = false) String traineeName
  ) {

    try {
      Trainer trainer = trainerService.findTrainerByUsername(userName);
      if (trainer == null) {
        throw new EntityNotFoundException("Trainer with username " + userName + " not found.");
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

      return ResponseEntity.ok(responseDTOList);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while fetching the trainer trainings.");
    }
  }

  public Date addDurationToTrainingDate(Date trainingDate, Number trainingDurationInMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.MINUTE, trainingDurationInMinutes.intValue());
    return calendar.getTime();
  }


}
