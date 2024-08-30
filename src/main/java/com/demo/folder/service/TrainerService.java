package com.demo.folder.service;

import com.demo.folder.dao.TrainerDAO;
import com.demo.folder.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);
    private TrainerDAO<Trainer> trainerDAO;

    @Autowired
    public TrainerService(TrainerDAO<Trainer> trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    public TrainerDAO<Trainer> getTrainerDAO() {
        return trainerDAO;
    }
}