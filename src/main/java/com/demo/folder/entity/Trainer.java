package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Trainer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "Specialization", referencedColumnName = "id")
  private TrainingType specialization;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "User_Id", referencedColumnName = "id")
  private User user;

  @OneToMany(mappedBy = "trainer")
  private List<Training> trainings;

  @ManyToMany
  @JoinTable(
      name = "trainer_trainee",
      joinColumns = @JoinColumn(name = "trainer_id"),
      inverseJoinColumns = @JoinColumn(name = "trainee_id")
  )
  private List<Trainee> trainees;

  public Trainer() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TrainingType getSpecialization() {
    return specialization;
  }

  public void setSpecialization(TrainingType specialization) {
    this.specialization = specialization;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Training> getTrainings() {
    return trainings;
  }

  public void setTrainings(List<Training> trainings) {
    this.trainings = trainings;
  }

  public List<Trainee> getTrainees() {
    return trainees;
  }

  public void setTrainees(List<Trainee> trainees) {
    this.trainees = trainees;
  }
}
