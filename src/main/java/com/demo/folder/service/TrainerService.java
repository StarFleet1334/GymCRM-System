package com.demo.folder.service;

import com.demo.folder.dao.TrainerDAO;
import com.demo.folder.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
    private TrainerDAO trainerDAO;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    public TrainerDAO getTrainerDAO() {
        return trainerDAO;
    }

    public Trainer getTrainer(Long userId) {
        return trainerDAO.read(userId);
    }

    public List<Trainer> getAllTrainers() {
        return trainerDAO.getAll();
    }


}