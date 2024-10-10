package com.demo.folder.controller.implementation;

import com.demo.folder.controller.skeleton.TraineeControllerInterface;
import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.dto.request.CreateTraineeRequestDTO;
import com.demo.folder.entity.dto.request.AllTraineeRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeProfileRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTraineeTrainersRequestDTO;
import com.demo.folder.entity.dto.response.TraineeResponseProfileDTO;
import com.demo.folder.entity.dto.response.TraineeResponse;
import com.demo.folder.entity.dto.response.UpdateTraineeTrainersResponseDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.health.prome.CustomMetrics;
import com.demo.folder.health.prome.TraineeExecutionTime;
import com.demo.folder.service.LoginAttemptService;
import com.demo.folder.service.TraineeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.demo.folder.utils.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TraineeController implements TraineeControllerInterface {

  @Autowired
  private TraineeService traineeService;

  @Autowired
  private TraineeExecutionTime traineeExecutionTime;

  @Autowired
  private CustomMetrics customMetrics;

  @Autowired
  private LoginAttemptService loginAttemptService;

  @Override
  public ResponseEntity<TraineeResponse> registerTrainee(
      @Valid @RequestBody CreateTraineeRequestDTO traineeRequestDTO,
      BindingResult result) {
    return traineeExecutionTime.recordExecutionTime(() -> {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest().body(null);
      }

      String[] registeredTrainee = traineeService.createTrainee(traineeRequestDTO);
      loginAttemptService.clearAttempts(registeredTrainee[1]);
      TraineeResponse response = new TraineeResponse();
      response.setUsername(registeredTrainee[1]);
      response.setPassword(registeredTrainee[0]);

      customMetrics.incrementTraineeRegistrationCount();

      URI location = ServletUriComponentsBuilder.fromCurrentRequest()
          .path("/{username}")
          .buildAndExpand(response.getUsername())
          .toUri();


      return ResponseEntity.created(location).body(response);
    });
  }


  @Override
  public ResponseEntity<List<AllTraineeRequestDTO>> getAllTrainee() {
    return ResponseEntity.ok(traineeService.getAllTraineesDetails());
  }


  @Override
  public ResponseEntity<String> changeTraineeAccountState(@PathVariable String username,
      @PathVariable StatusAction statusAction) {
    try {
      return switch (statusAction) {
        case ACTIVATE -> {
          traineeService.modifyTraineeState(username,true);
          yield ResponseEntity.ok("Trainee activated");
        }
        case DEACTIVATE -> {
          traineeService.modifyTraineeState(username,false);
          yield ResponseEntity.ok("Trainee de-activated");
        }
      };
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }
  }


  @Override
  public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
    try {
      Trainee trainee = traineeService.findTraineeByUsername(username);
      if (trainee == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Trainee with given username not found");
      }
      traineeService.deleteTraineeByIdVolTwo(trainee.getId());
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<Object> getTraineeProfile(@PathVariable String username) {
    try {
      TraineeResponseProfileDTO profile = traineeService.getTraineeProfileByUsername(username);
      return ResponseEntity.ok(profile);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }


  @Override
  public ResponseEntity<String> updateTraineeTrainers(
      @PathVariable String username,
      @Valid @RequestBody UpdateTraineeTrainersRequestDTO requestDTO,
      @PathVariable TraineeAction traineeAction, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      return switch (traineeAction) {
        case ADD -> {
          traineeService.updateTraineeTrainersAdd(username, requestDTO);
          yield ResponseEntity.ok("Trainer added");
        }
        case REMOVE -> {
          traineeService.updateTraineeTrainersRemove(username, requestDTO);
          yield ResponseEntity.ok("Trainer removed");
        }
      };
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<Object> updateTrainee(
      @PathVariable String username,
      @Valid @RequestBody UpdateTraineeProfileRequestDTO requestDTO, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      return ResponseEntity.ok(traineeService.updateTrainee(username, requestDTO));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<Object> getUnassignedTrainers(@PathVariable String username) {
    try {
      UpdateTraineeTrainersResponseDTO unassignedTrainers = traineeService.getUnassignedActiveTrainers(
          username);
      return ResponseEntity.ok(unassignedTrainers);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<Object> getTrainings(
      @PathVariable String userName,
      @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
      @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
      @RequestParam(name = "trainingName", required = false) String trainingName,
      @RequestParam(name = "trainingType", required = false) String trainingType
  ) {
    try {
      return ResponseEntity.ok(
          traineeService.getFilteredTrainings(userName, periodFrom, periodTo, trainingName,
              trainingType));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

}
