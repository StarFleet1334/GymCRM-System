package com.demo.folder.entity.base;

import jakarta.persistence.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "training_types")
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
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTrainingTypeName() {
    return trainingTypeName;
  }

  public void setTrainingTypeName(String trainingTypeName) {
    this.trainingTypeName = trainingTypeName;
  }

  public List<Training> getTrainings() {
    return trainings;
  }

  public void setTrainings(List<Training> trainings) {
    this.trainings = trainings;
  }

  public List<Trainer> getTrainers() {
    return trainers;
  }

  public void setTrainers(List<Trainer> trainers) {
    this.trainers = trainers;
  }
}
