package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class TrainingType {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrainingType.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "Training_Type_Name", nullable = false)
  private String trainingTypeName;

  @OneToMany(mappedBy = "trainingType")
  private List<Training> trainings;

  @OneToMany(mappedBy = "specialization")
  private List<Trainer> trainers;

  public TrainingType() {
    LOGGER.info("A new TrainingType object has been created.");
  }

  public Long getId() {
    LOGGER.debug("getId() called - returning {}", id);
    return id;
  }

  public void setId(Long id) {
    LOGGER.debug("setId() called with {}", id);
    this.id = id;
  }

  public String getTrainingTypeName() {
    LOGGER.debug("getTrainingTypeName() called - returning {}", trainingTypeName);
    return trainingTypeName;
  }

  public void setTrainingTypeName(String trainingTypeName) {
    LOGGER.info("setTrainingTypeName() called with {}", trainingTypeName);
    this.trainingTypeName = trainingTypeName;
  }

  public List<Training> getTrainings() {
    LOGGER.debug("getTrainings() called - returning list of size {}", trainings != null ? trainings.size() : 0);
    return trainings;
  }

  public void setTrainings(List<Training> trainings) {
    LOGGER.info("setTrainings() called with a list of size {}", trainings != null ? trainings.size() : 0);
    this.trainings = trainings;
  }

  public List<Trainer> getTrainers() {
    LOGGER.debug("getTrainers() called - returning list of size {}", trainers != null ? trainers.size() : 0);
    return trainers;
  }

  public void setTrainers(List<Trainer> trainers) {
    LOGGER.info("setTrainers() called with a list of size {}", trainers != null ? trainers.size() : 0);
    this.trainers = trainers;
  }
}
