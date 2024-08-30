package com.demo.folder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Training {
    private static final Logger LOGGER = LoggerFactory.getLogger(Training.class);
    private static long trainingIdCounter = 1;
    private Long trainingId;
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private Date trainingDate;
    private int trainingDuration;

    public Training() {
        this.trainingId = trainingIdCounter++;
        this.traineeId = null;
        this.trainerId = null;
        this.trainingName = "Unnamed Training";
        this.trainingType = TrainingType.NULL;
        this.trainingDate = null;
        this.trainingDuration = 0;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public void describe() {
        String msg = "Training{" +
                "trainingId=" + trainingId +
                ", traineeId=" + traineeId +
                ", trainerId=" + trainerId +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
        LOGGER.info(msg);
    }
}
