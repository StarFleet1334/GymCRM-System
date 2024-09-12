package com.demo.folder.entity;

import com.demo.folder.service.TraineeService;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Trainee {
  private static final Logger LOGGER = LoggerFactory.getLogger(Trainee.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "Date_Of_Birth", nullable = true)
  private Date dateOfBirth;
  @Column(name = "Address", nullable = true)
  private String address;

  @OneToMany(mappedBy = """
      trainee""", fetch = FetchType.EAGER)
  private List<Training> trainings;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "User_Id", referencedColumnName = "id")
  private User user;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "trainee_trainer",
      joinColumns = @JoinColumn(name = "trainee_id"),
      inverseJoinColumns = @JoinColumn(name = "trainer_id")
  )
  private List<Trainer> trainers;

  public Trainee() {
    LOGGER.info("A new Trainee object is created.");
  }

  public Long getId() {
    LOGGER.debug("getId() called - returning {}", id);
    return id;
  }

  public void setId(Long id) {
    LOGGER.debug("setId() called with {}", id);
    this.id = id;
  }

  public Date getDateOfBirth() {
    LOGGER.debug("getDateOfBirth() called - returning {}", dateOfBirth);
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    LOGGER.info("setDateOfBirth() called with {}", dateOfBirth);
    this.dateOfBirth = dateOfBirth;
  }

  public String getAddress() {
    LOGGER.debug("getAddress() called - returning {}", address);
    return address;
  }

  public void setAddress(String address) {
    LOGGER.info("setAddress() called with {}", address);
    this.address = address;
  }

  public User getUser() {
    LOGGER.debug("getUser() called - returning User with username: {}", (user != null) ? user.getUsername() : null);
    return user;
  }

  public void setUser(User user) {
    LOGGER.info("setUser() called with User: {}", user.getUsername());
    this.user = user;
  }

  public List<Training> getTrainings() {
    LOGGER.debug("getTrainings() called - returning {} trainings", (trainings != null) ? trainings.size() : 0);
    return trainings;
  }

  public void setTrainings(List<Training> trainings) {
    LOGGER.info("setTrainings() called with a list of {} trainings", (trainings != null) ? trainings.size() : 0);
    this.trainings = trainings;
  }

  public List<Trainer> getTrainers() {
    LOGGER.debug("getTrainers() called - returning {} trainers", (trainers != null) ? trainers.size() : 0);
    return trainers;
  }

  public void setTrainers(List<Trainer> trainers) {
    LOGGER.info("setTrainers() called with a list of {} trainers", (trainers != null) ? trainers.size() : 0);
    this.trainers = trainers;
  }

}
