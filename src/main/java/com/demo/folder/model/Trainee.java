package com.demo.folder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Trainee extends User {

  private static final Logger LOGGER = LoggerFactory.getLogger(Trainee.class);
  private static long traineeIdCounter = 1;
  private Date dateOfBirth;
  private String address;
  private List<Training> training;

  public Trainee() {
    this.userId = traineeIdCounter++;
    this.training = new ArrayList<>();
    this.address = "Unknown";
    this.setFirstName("Unknown");
    this.setLastName("Unknown");
  }


  public static long getTraineeIdCounter() {
    return traineeIdCounter;
  }

  public static void setTraineeIdCounter(long traineeIdCounter) {
    Trainee.traineeIdCounter = traineeIdCounter;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public List<Training> getTraining() {
    return training;
  }

  public void setTraining(List<Training> training) {
    this.training = training;
  }

  public void addTraining(Training training) {
    this.training.add(training);
  }

  public void describe() {
    String trainingInfo = training.isEmpty() ? "Not Enrolled" : training.stream()
        .map(Training::getTrainingName)
        .collect(Collectors.joining(", "));

    String msg = "Trainee{" +
        "userId=" + getUserId() +
        ", firstName='" + getFirstName() + '\'' +
        ", lastName='" + getLastName() + '\'' +
        ", username='" + getUsername() + '\'' +
        ", password='" + getPassword() + '\'' +
        ", isActive=" + isActive() +
        ", dateOfBirth=" + dateOfBirth +
        ", address='" + address + '\'' +
        ", trainings='" + trainingInfo + '\'' +
        '}';
    LOGGER.info(msg);
  }
}
