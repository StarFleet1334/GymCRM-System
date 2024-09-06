package com.demo.folder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

  private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
  protected long userId;
  protected String firstName;
  protected String lastName;
  protected String username;
  private String password;
  private boolean isActive;

  // User Password gets's initially null!
  public User() {
    this.password = null;
  }

  public Long getUserId() {
    return userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    if (firstName.isEmpty()) {
      LOGGER.error("firstName is null or empty");
    }
    this.firstName = firstName;
    updateUsername();
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    if (lastName.isEmpty()) {
      LOGGER.error("lastName is null or empty");
    }
    this.lastName = lastName;
    updateUsername();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void describe() {
    String msg = "User{" +
        "userId=" + userId +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", isActive=" + isActive +
        '}';
    LOGGER.info(msg);
  }

  private void updateUsername() {
    if (firstName != null && lastName != null && userId > 0) {
      this.username = firstName + "." + lastName;
    }
  }
}
