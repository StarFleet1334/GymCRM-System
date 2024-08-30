package com.demo.folder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer extends User {
    private static final Logger LOGGER = LoggerFactory.getLogger(Trainer.class);
    private static long trainerIdCounter = 1;
    private String specialization;
    private List<Training> training;
    private List<TrainingType> trainingType;

    public Trainer() {
        this.userId = trainerIdCounter++;
        this.trainingType = new ArrayList<>();
        this.training = new ArrayList<>();
    }

    public static long getTrainerIdCounter() {
        return trainerIdCounter;
    }

    public static void setTrainerIdCounter(long trainerIdCounter) {
        Trainer.trainerIdCounter = trainerIdCounter;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<Training> getTraining() {
        return training;
    }

    public void setTraining(List<Training> training) {
        this.training = training;
    }

    public List<TrainingType> getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(List<TrainingType> trainingType) {
        this.trainingType = trainingType;
    }

    public void describe() {
        String trainingInfo = training.isEmpty() ? "No Trainings" : training.stream()
                .map(Training::getTrainingName)
                .collect(Collectors.joining(", "));

        String trainingTypeInfo = trainingType.isEmpty() ? "No Training Types" : trainingType.stream()
                .map(TrainingType::getTrainingTypeName)
                .collect(Collectors.joining(", "));

        String msg = "Trainer{" +
                "userId=" + getUserId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", isActive=" + isActive() +
                ", specialization='" + specialization + '\'' +
                ", trainings='" + trainingInfo + '\'' +
                ", trainingTypes='" + trainingTypeInfo + '\'' +
                '}';
        LOGGER.info(msg);
    }
}
