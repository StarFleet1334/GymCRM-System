package com.demo.folder.controller;


import com.demo.folder.entity.base.Training;
import com.demo.folder.entity.dto.request.TrainingRequestDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/training", produces = {"application/JSON", "application/XML"})
@Tag(name = "Training Controller", description = "Operations related to trainings")

public class TrainingController {

  @Autowired
  private TrainingService trainingService;

  @PostMapping("/create")
  @Operation(summary = "Create Training", description = "Creates a new training session.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Training created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Related entity not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> create(@Valid @RequestBody TrainingRequestDTO trainingRequestDTO) {
    try {
      trainingService.createTraining(trainingRequestDTO);
      return ResponseEntity.ok("Training created successfully");
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while creating the training.");
    }
  }

  @GetMapping("/all")
  @Operation(summary = "Get All Trainings", description = "Retrieves all training sessions.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all trainings"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TrainingRequestDTO>> getAll() {
    try {
      List<Training> trainings = trainingService.getAllTrainings();
      List<TrainingRequestDTO> dtoList = new ArrayList<>();
      for (Training training : trainings) {
        TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO();
        trainingRequestDTO.setTraineeUserName(training.getTrainee().getUser().getUsername());
        trainingRequestDTO.setTrainerUserName(training.getTrainer().getUser().getUsername());
        trainingRequestDTO.setTrainingName(training.getTrainingName());
        trainingRequestDTO.setTrainingDate(training.getTrainingDate());
        trainingRequestDTO.setDuration(training.getTrainingDuration());
        dtoList.add(trainingRequestDTO);
      }
      return ResponseEntity.ok(dtoList);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
