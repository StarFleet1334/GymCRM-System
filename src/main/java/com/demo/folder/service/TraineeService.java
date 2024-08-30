package com.demo.folder.service;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
    private TraineeDAO traineeDAO;
    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public TraineeDAO getTraineeDAO() {
        return traineeDAO;
    }

    public Trainee getTrainee(Long userId) {
        return traineeDAO.read(userId);
    }

    public List<Trainee> getAllTrainees() {
        return traineeDAO.getAll();
    }
}
