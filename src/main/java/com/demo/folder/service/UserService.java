package com.demo.folder.service;

import com.demo.folder.entity.base.User;
import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.repository.UserRepository;
import com.demo.folder.utils.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoginAttemptService loginAttemptService;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      LoginAttemptService loginAttemptService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.loginAttemptService = loginAttemptService;
  }

  @Transactional
  public User authenticate(String username, String password) {
    LOGGER.info("Authenticating user with username: {}", username);
    User user = userRepository.findByUsernameWithAssociations(username);
    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
      LOGGER.info("Authentication successful for username: {}", username);
      return user;
    }
    LOGGER.warn("Authentication failed for username: {}", username);
    throw new AuthenticationException("Invalid credentials");
  }

  @Transactional
  public void createUser(User user) {
    LOGGER.info("Creating new user: {}", user.getUsername());
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    LOGGER.info("Fetching all users");
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      throw new EntityNotFoundException("No users found.");
    }
    return users;
  }

  @Transactional(readOnly = true)
  public boolean usernameExists(String username) {
    LOGGER.info("Checking if username exists: {}", username);
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }
    return userRepository.findByUsername(username) != null;
  }


  @Transactional(readOnly = true)
  public List<String> findUsernamesStartingWith(String baseUsername) {
    LOGGER.info("Finding usernames starting with: {}", baseUsername);
    if (baseUsername == null || baseUsername.isEmpty()) {
      throw new IllegalArgumentException("Base username cannot be null or empty.");
    }
    return userRepository.findUsernamesStartingWith(baseUsername);
  }

  @Transactional
  public void updatePassword(User user, String newPassword) {
    LOGGER.info("Updating password for user: {}", user.getUsername());
    if (user == null) {
      throw new IllegalArgumentException("User object cannot be null.");
    }
    if (newPassword == null || newPassword.isEmpty()) {
      throw new IllegalArgumentException("New password cannot be null or empty.");
    }
    FileUtil.writeCredentialsToFile("trainee_credentials.txt", user.getUsername(), newPassword);
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Transactional
  public void updateUser(User user) {
    LOGGER.info("Updating user: {}", user.getUsername());
    if (user == null) {
      throw new IllegalArgumentException("User object cannot be null.");
    }
    userRepository.save(user);
  }
}
