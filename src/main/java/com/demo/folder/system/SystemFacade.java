package com.demo.folder.system;

import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingService;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SystemFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemFacade.class);
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final StorageBean storageBean;

    @Autowired
    public SystemFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, StorageBean storageBean) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.storageBean = storageBean;
    }
    public TraineeService getTraineeService() {
        return traineeService;
    }
    public TrainingService getTrainingService() {
        return trainingService;
    }

    public TrainerService getTrainerService() {
        return trainerService;
    }
}

