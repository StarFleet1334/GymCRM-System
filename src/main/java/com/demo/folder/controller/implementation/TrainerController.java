package com.demo.folder.controller.implementation;


import com.demo.folder.controller.skeleton.TrainerControllerInterface;
import com.demo.folder.entity.dto.request.TrainerRequestDTO;
import com.demo.folder.entity.dto.request.UpdateTrainerProfileRequestDTO;
import com.demo.folder.entity.dto.response.TrainerProfileResponseDTO;
import com.demo.folder.entity.dto.response.TrainerResponseDTO;
import com.demo.folder.error.exception.EntityNotFoundException;
import com.demo.folder.health.prome.CustomMetrics;
import com.demo.folder.service.TrainerService;
import com.demo.folder.utils.StatusAction;
import com.demo.folder.utils.FileUtil;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TrainerController implements TrainerControllerInterface {

  @Autowired
  private TrainerService trainerService;

  @Autowired
  private CustomMetrics customMetrics;

  @Override
  public ResponseEntity<?> registerTrainer(@Valid @RequestBody TrainerRequestDTO trainerRequestDTO,
      BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
    }
    String[] registeredTrainer = trainerService.createTrainer(trainerRequestDTO);
    if (registeredTrainer == null) {
      throw new IllegalArgumentException(
          "Training type credential was incorrect or some other fields were indicated incorrectly.");
    }
    TrainerResponseDTO response = new TrainerResponseDTO();
    response.setUsername(registeredTrainer[1]);
    response.setPassword(registeredTrainer[0]);

    FileUtil.writeCredentialsToFile("trainer_credentials.txt", registeredTrainer[1],
        registeredTrainer[0]);
    customMetrics.incrementTrainerRegistrationCount();

    customMetrics.incrementTraineeRegistrationCount();
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{username}")
        .buildAndExpand(response.getUsername())
        .toUri();

    return ResponseEntity.created(location).body(response);

  }

  @Override
  public ResponseEntity<List<TrainerRequestDTO>> getAllTrainee() {
    return ResponseEntity.ok(trainerService.retrieveAllTrainers());
  }

  @Override
  public ResponseEntity<?> changeTrainerAccountState(@PathVariable String username,
      @PathVariable StatusAction statusAction) {
    try {
      return switch (statusAction) {
        case ACTIVATE -> {
          trainerService.activateTrainerRest(username);
          yield ResponseEntity.ok("Trainer activated");
        }
        case DEACTIVATE -> {
          trainerService.deActivateTrainerRest(username);
          yield ResponseEntity.ok("Trainer de-activated");
        }
      };
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TrainerProfileResponseDTO> getTrainerProfile(
      @PathVariable String username
  ) {
    TrainerProfileResponseDTO dto = trainerService.getTrainerProfile(username);
    return ResponseEntity.ok(dto);
  }

  @Override
  public ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(
      @PathVariable String username,
      @Valid @RequestBody UpdateTrainerProfileRequestDTO requestDTO
  ) {
    return ResponseEntity.ok(trainerService.updateTrainer(username, requestDTO));
  }

  @Override
  public ResponseEntity<?> getTrainings(
      @PathVariable String username,
      @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
      @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
      @RequestParam(name = "traineeName", required = false) String traineeName
  ) {

    try {
      return ResponseEntity.ok(
          trainerService.getFilteredTrainingsForTrainer(username, periodFrom, periodTo,
              traineeName));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
