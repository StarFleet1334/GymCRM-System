package com.demo.folder.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Trainee {

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
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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

  public List<Trainer> getTrainers() {
    return trainers;
  }

  public void setTrainers(List<Trainer> trainers) {
    this.trainers = trainers;
  }

}
