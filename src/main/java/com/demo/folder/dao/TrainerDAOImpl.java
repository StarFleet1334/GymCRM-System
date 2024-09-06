package com.demo.folder.dao;

import com.demo.folder.model.Trainer;
import com.demo.folder.storage.StorageBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerDAOImpl implements TrainerDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDAOImpl.class);
  private StorageBean storageBean;

  @Autowired
  public TrainerDAOImpl(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  @Override
  public void create(Trainer entity) {
    Map<Long, Object> trainers = storageBean.getByNameSpace("trainers");
    trainers.put(entity.getUserId(), entity);
    LOGGER.info("User got created");
  }

  @Override
  public Trainer read(Long id) {
    Map<Long, Object> trainers = storageBean.getByNameSpace("trainers");
    return (Trainer) trainers.get(id);
  }

  @Override
  public void update(Trainer entity) {
    storageBean.getByNameSpace("trainers").put(entity.getUserId(), entity);
    LOGGER.info("User got updated");
  }

  @Override
  public List<Trainer> getAll() {
    Map<Long, Object> trainers = storageBean.getByNameSpace("trainers");
    return trainers.values().stream().map(u -> (Trainer) u).collect(Collectors.toList());
  }
}
