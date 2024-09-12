package com.demo.folder.service;

import com.demo.folder.entity.User;
import com.demo.folder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User authenticate(String username, String password) {
    LOGGER.info("Authenticating user with username: {}", username);
    User user = userRepository.findByUsernameWithAssociations(username);
    if (user != null && user.getPassword().equals(password)) {
      LOGGER.info("Authentication successful for username: {}", username);
      return user;
    }
    LOGGER.warn("Authentication failed for username: {}", username);
    return null;
  }

  @Transactional
  public void createUser(User user) {
    LOGGER.info("Creating new user: {}", user.getUsername());
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    LOGGER.info("Fetching all users");
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public boolean usernameExists(String username) {
    LOGGER.info("Checking if username exists: {}", username);
    return userRepository.findByUsername(username) != null;
  }


  @Transactional(readOnly = true)
  public List<String> findUsernamesStartingWith(String baseUsername) {
    LOGGER.info("Finding usernames starting with: {}", baseUsername);
    return userRepository.findUsernamesStartingWith(baseUsername);
  }

  @Transactional
  public void updatePassword(User user, String newPassword) {
    LOGGER.info("Updating password for user: {}", user.getUsername());
    user.setPassword(newPassword);
    userRepository.save(user);
  }

  @Transactional
  public void updateUser(User user) {
    LOGGER.info("Updating user: {}", user.getUsername());
    userRepository.save(user);
  }
}
