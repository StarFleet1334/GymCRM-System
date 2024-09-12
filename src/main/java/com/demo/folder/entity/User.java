package com.demo.folder.entity;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "`User`")
public class User {
  private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "First_Name", nullable = false)
  private String firstName;

  @Column(name = "Last_Name", nullable = false)
  private String lastName;

  @Column(name = "Username", nullable = false, unique = true)
  private String username;

  @Column(name = "Password", nullable = false)
  private String password;

  @Column(name = "IsActive", nullable = false)
  private boolean isActive;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private Trainee trainee;

  @OneToOne(mappedBy = "user")
  private Trainer trainerS;

  public User() {
    LOGGER.info("A new User object has been created.");
  }


  public Long getId() {
    LOGGER.debug("getId() called - returning {}", id);
    return id;
  }

  public void setId(Long id) {
    LOGGER.debug("setId() called with {}", id);
    this.id = id;
  }

  public String getFirstName() {
    LOGGER.debug("getFirstName() called - returning {}", firstName);
    return firstName;
  }

  public void setFirstName(String firstName) {
    LOGGER.info("setFirstName() called with {}", firstName);
    this.firstName = firstName;
  }

  public String getLastName() {
    LOGGER.debug("getLastName() called - returning {}", lastName);
    return lastName;
  }

  public void setLastName(String lastName) {
    LOGGER.info("setLastName() called with {}", lastName);
    this.lastName = lastName;
  }

  public String getPassword() {
    LOGGER.debug("getPassword() called - returning [PROTECTED]");
    return password;
  }

  public void setPassword(String password) {
    LOGGER.info("setPassword() called - password updated [PROTECTED]");
    this.password = password;
  }

  public String getUsername() {
    LOGGER.debug("getUsername() called - returning {}", username);

    return username;
  }

  public void setUsername(String username) {
    LOGGER.info("setUsername() called with {}", username);
    this.username = username;
  }

  public boolean isActive() {
    LOGGER.debug("isActive() called - returning {}", isActive);
    return isActive;
  }

  public void setActive(boolean active) {
    LOGGER.info("setActive() called with {}", active);
    isActive = active;
  }

  public Trainee getTraineeS() {
    LOGGER.debug("getTraineeS() called");
    return trainee;
  }

  public void setTraineeS(Trainee trainee) {
    LOGGER.info("setTraineeS() called");
    this.trainee = trainee;
  }

  public Trainer getTrainerS() {
    LOGGER.debug("getTrainerS() called");
    return trainerS;
  }

  public void setTrainerS(Trainer trainerS) {
    LOGGER.info("setTrainerS() called");
    this.trainerS = trainerS;
  }
}
