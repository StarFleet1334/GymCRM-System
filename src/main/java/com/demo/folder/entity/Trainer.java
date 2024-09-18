package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "trainers")
public class Trainer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Trainer.class);

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

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "trainer_trainee",
      joinColumns = @JoinColumn(name = "trainer_id"),
      inverseJoinColumns = @JoinColumn(name = "trainee_id")
  )
  private List<Trainee> trainees;

  public Trainer() {
    LOGGER.info("A new Trainer object has been created.");
  }

  public Long getId() {
    LOGGER.debug("getId() called - returning {}", id);
    return id;
  }

  public void setId(Long id) {
    LOGGER.debug("setId() called with {}", id);
    this.id = id;
  }

  public TrainingType getSpecialization() {
    LOGGER.debug("getSpecialization() called - returning {}", specialization != null ? specialization.getTrainingTypeName() : "null");
    return specialization;
  }

  public void setSpecialization(TrainingType specialization) {
    LOGGER.info("setSpecialization() called with specialization: {}", specialization.getTrainingTypeName());
    this.specialization = specialization;
  }

  public User getUser() {
    LOGGER.debug("getUser() called - returning User: {}", user != null ? user.getUsername() : "null");
    return user;
  }

  public void setUser(User user) {
    LOGGER.info("setUser() called with User: {}", user.getUsername());
    this.user = user;
  }

  public List<Training> getTrainings() {
    LOGGER.debug("getTrainings() called - returning {} trainings", trainings != null ? trainings.size() : 0);
    return trainings;
  }

  public void setTrainings(List<Training> trainings) {
    LOGGER.info("setTrainings() called with a list of {} trainings", trainings != null ? trainings.size() : 0);
    this.trainings = trainings;
  }

  public List<Trainee> getTrainees() {
    LOGGER.debug("getTrainees() called - returning {} trainees", trainees != null ? trainees.size() : 0);
    return trainees;
  }

  public void setTrainees(List<Trainee> trainees) {
    LOGGER.info("setTrainees() called with a list of {} trainees", trainees != null ? trainees.size() : 0);
    this.trainees = trainees;
  }
}
