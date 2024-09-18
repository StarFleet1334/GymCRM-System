package com.demo.folder.service;


import com.demo.folder.entity.TrainingType;
import com.demo.folder.repository.TrainingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingTypeService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeService.class);
  @Autowired
  private TrainingTypeRepository trainingTypeRepository;

  @Transactional
  public void createTrainingType(TrainingType trainingType) {
    LOGGER.info("Creating new training type: {}", trainingType.getTrainingTypeName());
    trainingTypeRepository.save(trainingType);
  }

  @Transactional(readOnly = true)
  public List<TrainingType> getAllTrainingTypes() {
    LOGGER.info("Fetching all training types");
    List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
    if (trainingTypes.isEmpty()) {
      throw new EntityNotFoundException("No training types found.");
    }

    return trainingTypes;
  }
}
