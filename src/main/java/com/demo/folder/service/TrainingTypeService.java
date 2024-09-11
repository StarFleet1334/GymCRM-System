package com.demo.folder.service;


import com.demo.folder.entity.TrainingType;
import com.demo.folder.repository.TrainingTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingTypeService {

  @Autowired
  private TrainingTypeRepository trainingTypeRepository;

  @Transactional
  public void createTrainingType(TrainingType trainingType) {
    trainingTypeRepository.save(trainingType);
  }

  @Transactional(readOnly = true)
  public List<TrainingType> getAllTrainingTypes() {
    return trainingTypeRepository.findAll();
  }
}
