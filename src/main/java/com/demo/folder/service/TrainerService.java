package com.demo.folder.service;

import com.demo.folder.entity.Trainer;
import com.demo.folder.repository.TrainerRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

  @Autowired
  private TrainerRepository trainerRepository;

  @Transactional
  public Trainer createTrainer(Trainer trainer) {
    trainerRepository.save(trainer);
    return trainer;
  }

  @Transactional(readOnly = true)
  public Trainer findTrainerByUsername(String username) {
    Trainer trainer = trainerRepository.findByUsername(username);
    if (trainer != null) {
      Hibernate.initialize(trainer.getTrainees());
    }
    return trainer;
  }

  @Transactional
  public void activateTrainer(Long trainerId) {
    trainerRepository.updateTrainerStatus(trainerId, true);
  }

  @Transactional
  public void deactivateTrainer(Long trainerId) {
    trainerRepository.updateTrainerStatus(trainerId, false);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getAllTrainers() {
    return trainerRepository.findAll();
  }

  @Transactional
  public void updateTrainer(Trainer trainer) {
    trainerRepository.save(trainer);
  }

  @Transactional(readOnly = true)
  public List<Trainer> getUnassignedTrainers(List<Trainer> assignedTrainers) {
    return trainerRepository.findUnassignedTrainers(assignedTrainers);
  }


}
