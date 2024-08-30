package com.demo.folder.processor;

import com.demo.folder.model.Trainer;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.demo.folder.utils.StorageUtil.TRAINERS_NAMESPACE;

@Component
public class TrainerProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TrainerProcessor.class);
  private final StorageBean storageBean;

  @Autowired
  public TrainerProcessor(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  public void process(String line) {
    String[] parts = line.split(",");
    if (parts.length < 7) {
      LOGGER.warn("Invalid trainer data: {}", line);
      return;
    }

    try {
      Trainer trainer = new Trainer();
      trainer.setFirstName(parts[1]);
      trainer.setLastName(parts[2]);
      trainer.setUsername(parts[3]);
      trainer.setPassword(parts[4]);
      trainer.setSpecialization(parts[5]);
      trainer.setActive(Boolean.parseBoolean(parts[6]));
      storageBean.getByNameSpace(TRAINERS_NAMESPACE).put(trainer.getUserId(), trainer);
      LOGGER.info("Added Trainer: {}", trainer);
    } catch (Exception e) {
      LOGGER.error("Error processing trainer data: {}", line, e);
    }
  }
}
