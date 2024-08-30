package com.demo.folder.daoImpl;

import com.demo.folder.dao.TraineeDAO;
import com.demo.folder.model.Trainee;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TraineeDAOImpl implements TraineeDAO {

  private StorageBean storageBean;
  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDAOImpl.class);

  @Autowired
  public TraineeDAOImpl(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  @Override
  public void create(Trainee entity) {
    Map<Long, Object> trainees = storageBean.getByNameSpace("trainees");
    trainees.put(entity.getUserId(), entity);
    LOGGER.info("Trainee got created: {}", entity.getUsername());
  }

  @Override
  public Trainee read(Long id) {
    Map<Long, Object> trainees = storageBean.getByNameSpace("trainees");
    return (Trainee) trainees.get(id);
  }

  @Override
  public void update(Trainee entity) {
    storageBean.getByNameSpace("trainees").put(entity.getUserId(), entity);
    LOGGER.info("Trainee got updated: {}", entity.getUsername());
  }

  @Override
  public void delete(Long id) {
    storageBean.getByNameSpace("trainees").remove(id);
    LOGGER.info("Trainee got deleted with id: {}", id);
  }

  @Override
  public List<Trainee> getAll() {
    Map<Long, Object> trainees = storageBean.getByNameSpace("trainees");
    return trainees.values().stream().map(u -> (Trainee) u).collect(Collectors.toList());
  }
}
