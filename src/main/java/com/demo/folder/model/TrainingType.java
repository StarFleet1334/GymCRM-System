package com.demo.folder.model;

public enum TrainingType {
    ONLINE("Online"),
    OFFLINE("Offline"),
    HYBRID("Hybrid"),
    NULL("null");
    private final String  trainingTypeName;
    TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
    public String getTrainingTypeName() {
        return trainingTypeName;
    }
}
