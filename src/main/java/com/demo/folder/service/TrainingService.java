package com.demo.folder.service;

import com.demo.folder.dao.TrainingDAO;
import com.demo.folder.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);
    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }


    public Training getTraining(Long trainingId) {
        return trainingDAO.read(trainingId);
    }

    public List<Training> getAllTrainings() {
        return trainingDAO.getAll();
    }
}
