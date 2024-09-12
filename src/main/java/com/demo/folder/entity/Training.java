package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Training {
  private static final Logger LOGGER = LoggerFactory.getLogger(Training.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "Trainee_Id", referencedColumnName = "id", nullable = true)
  private Trainee trainee;

  @ManyToOne
  @JoinColumn(name = "Trainer_Id", referencedColumnName = "id")
  private Trainer trainer;

  @Column(name = "Training_Name", nullable = false)
  private String trainingName;

  @ManyToOne
  @JoinColumn(name = "Training_Type_Id", referencedColumnName = "id")
  private TrainingType trainingType;

  @Column(name = "Training_Date", nullable = false)
  private Date trainingDate;

  @Column(name = "Training_Duration", nullable = false)
  private Number trainingDuration;

  public Training() {
    LOGGER.info("A new Training object has been created.");
  }

  public Long getId() {
    LOGGER.debug("getId() called - returning {}", id);
    return id;
  }

  public void setId(Long id) {
    LOGGER.debug("setId() called with {}", id);
    this.id = id;
  }

  public Trainee getTrainee() {
    LOGGER.debug("getTrainee() called - returning Trainee: {}", trainee != null ? trainee.getUser().getUsername() : "null");
    return trainee;
  }

  public void setTrainee(Trainee trainee) {
    LOGGER.info("setTrainee() called with Trainee: {}", trainee != null ? trainee.getUser().getUsername() : "null");
    this.trainee = trainee;
  }

  public Trainer getTrainer() {
    LOGGER.debug("getTrainer() called - returning Trainer: {}", trainer != null ? trainer.getUser().getUsername() : "null");
    return trainer;
  }

  public void setTrainer(Trainer trainer) {
    LOGGER.info("setTrainer() called with Trainer: {}", trainer != null ? trainer.getUser().getUsername() : "null");
    this.trainer = trainer;
  }

  public String getTrainingName() {
    LOGGER.debug("getTrainingName() called - returning {}", trainingName);
    return trainingName;
  }

  public void setTrainingName(String trainingName) {
    LOGGER.info("setTrainingName() called with {}", trainingName);
    this.trainingName = trainingName;
  }

  public TrainingType getTrainingType() {
    LOGGER.debug("getTrainingType() called - returning {}", trainingType != null ? trainingType.getTrainingTypeName() : "null");
    return trainingType;
  }

  public void setTrainingType(TrainingType trainingType) {
    LOGGER.info("setTrainingType() called with {}", trainingType != null ? trainingType.getTrainingTypeName() : "null");
    this.trainingType = trainingType;
  }

  public Date getTrainingDate() {
    LOGGER.debug("getTrainingDate() called - returning {}", trainingDate);
    return trainingDate;
  }

  public void setTrainingDate(Date trainingDate) {
    LOGGER.info("setTrainingDate() called with {}", trainingDate);
    this.trainingDate = trainingDate;
  }

  public Number getTrainingDuration() {
    LOGGER.debug("getTrainingDuration() called - returning {}", trainingDuration);
    return trainingDuration;
  }

  public void setTrainingDuration(Number trainingDuration) {
    LOGGER.info("setTrainingDuration() called with {}", trainingDuration);
    this.trainingDuration = trainingDuration;
  }
}