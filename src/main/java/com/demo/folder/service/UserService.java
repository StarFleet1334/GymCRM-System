package com.demo.folder.service;

import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.User;
import com.demo.folder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User authenticate(String username, String password) {
    User user = userRepository.findByUsernameWithAssociations(username);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }
    return null;
  }

  @Transactional
  public void createUser(User user) {
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public boolean usernameExists(String username) {
    return userRepository.findByUsername(username) != null;
  }


  @Transactional(readOnly = true)
  public List<String> findUsernamesStartingWith(String baseUsername) {
    return userRepository.findUsernamesStartingWith(baseUsername);
  }

  @Transactional
  public void updatePassword(User user, String newPassword) {
    user.setPassword(newPassword);
    userRepository.save(user);
  }

  @Transactional
  public void updateUser(User user) {
    userRepository.save(user);
  }
}
