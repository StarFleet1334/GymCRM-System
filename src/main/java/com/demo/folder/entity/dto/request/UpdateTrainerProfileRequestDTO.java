package com.demo.folder.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTrainerProfileRequestDTO {

  @NotBlank(message = "userName is required")
  private String userName;
  @NotBlank(message = "firstName is required")
  private String firstName;
  @NotBlank(message = "lastName is required")
  private String lastName;

  private String specialization;

  @NotNull(message = "isActive is required")
  private boolean isActive;

  public String getUserName() {
    return userName;
  }

  public void setUserName(
      @NotBlank(message = "userName is required") String userName) {
    this.userName = userName;
  }

  public @NotBlank(message = "firstName is required") String getFirstName() {
    return firstName;
  }

  public void setFirstName(
      @NotBlank(message = "firstName is required") String firstName) {
    this.firstName = firstName;
  }

  public @NotBlank(message = "lastName is required") String getLastName() {
    return lastName;
  }

  public void setLastName(
      @NotBlank(message = "lastName is required") String lastName) {
    this.lastName = lastName;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  @NotNull(message = "isActive is required")
  public boolean isActive() {
    return isActive;
  }

  public void setActive(
      @NotNull(message = "isActive is required") boolean active) {
    isActive = active;
  }
}
