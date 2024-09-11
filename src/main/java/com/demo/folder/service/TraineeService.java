package com.demo.folder.service;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import com.demo.folder.repository.TraineeRepository;
import com.demo.folder.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TraineeService {

  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;

  @Autowired
  public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
    this.traineeRepository = traineeRepository;
    this.trainerRepository = trainerRepository;
  }

  @Transactional
  public void createTrainee(Trainee trainee) {
    traineeRepository.save(trainee);
  }

  @Transactional
  public Trainee findTraineeByUsername(String username) {
    return traineeRepository.findByUsername(username);
  }

  @Transactional(readOnly = true)
  public List<Trainee> getAllTrainees() {
    return traineeRepository.findAll();
  }


  @Transactional
  public void activateTrainee(Long traineeId) {
    traineeRepository.updateTraineeStatus(traineeId, true);
  }

  @Transactional
  public void deactivateTrainee(Long traineeId) {
    traineeRepository.updateTraineeStatus(traineeId, false);
  }

  @Transactional
  public void deleteTraineeById(Long traineeId) {
    traineeRepository.deleteById(traineeId);
  }

  @Transactional
  public void updateTrainee(Trainee trainee) {
    traineeRepository.save(trainee); // Update trainee
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAssignedTrainers(Trainee trainee) {
    return trainee.getTrainers();
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(Trainee trainee) {
    List<Trainer> assignedTrainers = trainee.getTrainers();
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }

}
