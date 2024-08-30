package com.demo.folder.service;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
    private TraineeDAO<Trainee> traineeDAO;
    @Autowired
    public void setTraineeDAO(TraineeDAO<Trainee> traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public TraineeDAO<Trainee> getTraineeDAO() {
        return traineeDAO;
    }
}
