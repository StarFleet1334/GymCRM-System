package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Training {

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
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Trainee getTrainee() {
    return trainee;
  }

  public void setTrainee(Trainee trainee) {
    this.trainee = trainee;
  }

  public Trainer getTrainer() {
    return trainer;
  }

  public void setTrainer(Trainer trainer) {
    this.trainer = trainer;
  }

  public String getTrainingName() {
    return trainingName;
  }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }

  public TrainingType getTrainingType() {
    return trainingType;
  }

  public void setTrainingType(TrainingType trainingType) {
    this.trainingType = trainingType;
  }

  public Date getTrainingDate() {
    return trainingDate;
  }

  public void setTrainingDate(Date trainingDate) {
    this.trainingDate = trainingDate;
  }

  public Number getTrainingDuration() {
    return trainingDuration;
  }

  public void setTrainingDuration(Number trainingDuration) {
    this.trainingDuration = trainingDuration;
  }
}
