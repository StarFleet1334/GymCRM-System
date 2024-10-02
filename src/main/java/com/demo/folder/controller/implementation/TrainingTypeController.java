package com.demo.folder.controller.implementation;

import com.demo.folder.controller.skeleton.TrainingTypeControllerInterface;
import com.demo.folder.entity.dto.request.TrainingTypeRequestDTO;
import com.demo.folder.service.TrainingTypeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TrainingTypeController implements TrainingTypeControllerInterface {

  @Autowired
  private TrainingTypeService trainingTypeService;

  @Override
  public ResponseEntity<?> createTrainingType(
      @Valid @RequestBody TrainingTypeRequestDTO trainingTypeRequestDTO, BindingResult result) {
    try {
      if (result.hasErrors()) {
        return ResponseEntity.badRequest()
            .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
      }
      trainingTypeService.createTrainingType(trainingTypeRequestDTO);
      URI location = ServletUriComponentsBuilder.fromCurrentRequest()
          .build()
          .toUri();
      return ResponseEntity.created(location).body("Training type created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TrainingTypeRequestDTO>> getAllTrainingTypes() {
    return ResponseEntity.ok(trainingTypeService.retrieveAllTrainingTypes());
  }

  @Override
  public ResponseEntity<TrainingTypeRequestDTO> getTrainingTypeById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(trainingTypeService.getTrainingTypeById(id));
  }

}
