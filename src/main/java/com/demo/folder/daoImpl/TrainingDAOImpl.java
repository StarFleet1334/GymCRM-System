package com.demo.folder.daoImpl;

import com.demo.folder.dao.TrainingDAO;
import com.demo.folder.model.Training;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TrainingDAOImpl implements TrainingDAO {
    private StorageBean storageBean;
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDAOImpl.class);

    @Autowired
    public void setStorageBean(StorageBean storageBean) {
        this.storageBean = storageBean;
    }

    @Override
    public void create(Training training) {
        Map<Long, Object> trainings = storageBean.getByNameSpace("trainings");
        trainings.put(training.getTrainingId(), training);
        LOGGER.info("Training got created");
    }

    @Override
    public Training read(Long id) {
        Map<Long, Object> trainings = storageBean.getByNameSpace("trainings");
        return (Training) trainings.get(id);
    }

    @Override
    public List<Training> getAll() {
        Map<Long, Object> trainings = storageBean.getByNameSpace("trainings");
        return trainings.values().stream().map(t -> (Training) t).collect(Collectors.toList());
    }
}
