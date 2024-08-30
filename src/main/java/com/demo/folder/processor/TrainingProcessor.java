package com.demo.folder.processor;

import com.demo.folder.model.Training;
import com.demo.folder.model.TrainingType;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static com.demo.folder.utils.StorageUtil.TRAININGS_NAMESPACE;

@Component
public class TrainingProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingProcessor.class);
    private final StorageBean storageBean;

    @Autowired
    public TrainingProcessor(StorageBean storageBean) {
        this.storageBean = storageBean;
    }

    public void process(String line) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            LOGGER.warn("Invalid training data: {}", line);
            return;
        }

        try {
            Training training = new Training();
            training.setTrainingId(Long.parseLong(parts[0]));
            training.setTraineeId(Long.parseLong(parts[1]));
            training.setTrainerId(Long.parseLong(parts[2]));
            training.setTrainingName(parts[3]);
            TrainingType trainingType = getTrainingType(parts[4]);
            if (trainingType == null) {
                LOGGER.warn("Invalid training type: {}", parts[4]);
                return;
            }
            training.setTrainingType(trainingType);
            training.setTrainingDate(new SimpleDateFormat("yyyy-MM-dd").parse(parts[5]));
            training.setTrainingDuration(Integer.parseInt(parts[6]));
            storageBean.getByNameSpace(TRAININGS_NAMESPACE).put(training.getTrainingId(), training);
            LOGGER.info("Added Training: {}", training);
        } catch (Exception e) {
            LOGGER.error("Error processing training data: {}", line, e);
        }
    }

    private TrainingType getTrainingType(String type) {
        try {
            return TrainingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid TrainingType: {}", type);
            return null;
        }
    }
}
