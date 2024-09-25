package com.demo.folder.controller;


import com.demo.folder.entity.base.TrainingType;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/training-type", produces = {"application/JSON", "application/XML"})
@Tag(name = "Training Type Controller", description = "Operations related to training types")
public class TrainingTypeController {

  @Autowired
  private TrainingTypeService trainingTypeService;

  @PostMapping("/create")
  @Operation(summary = "Create Training Type", description = "Creates a new training type.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created training type"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> createTrainingType(
      @Valid @RequestBody TrainingTypeRequestDTO trainingTypeRequestDTO, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      trainingTypeService.createTrainingType(trainingTypeRequestDTO);
      return ResponseEntity.ok("Training type created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while creating the training type.");
    }
  }

  @GetMapping("/all")
  @Operation(summary = "Get All Training Types", description = "Retrieves all training types.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all training types"),
      @ApiResponse(responseCode = "404", description = "No training types found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TrainingTypeRequestDTO>> getAllTrainingTypes() {
    List<TrainingType> list = trainingTypeService.getAllTrainingTypes();
    if (list.isEmpty()) {
      throw new EntityNotFoundException("No training types found.");
    }
    List<TrainingTypeRequestDTO> trainingTypeRequestDTOS = new ArrayList<>();
    for (TrainingType trainingType : list) {
      TrainingTypeRequestDTO trainingTypeRequestDTO = new TrainingTypeRequestDTO();
      trainingTypeRequestDTO.setId(trainingType.getId());
      trainingTypeRequestDTO.setTrainingTypeName(trainingType.getTrainingTypeName());
      trainingTypeRequestDTOS.add(trainingTypeRequestDTO);
    }
    return ResponseEntity.ok(trainingTypeRequestDTOS);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Training Type By ID", description = "Retrieves a training type by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved training type"),
      @ApiResponse(responseCode = "404", description = "Training type not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<TrainingTypeRequestDTO> getTrainingTypeById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(trainingTypeService.getTrainingTypeById(id));
  }

}
