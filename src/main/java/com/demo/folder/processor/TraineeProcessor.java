package com.demo.folder.processor;

import com.demo.folder.model.Trainee;
import com.demo.folder.storage.StorageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static com.demo.folder.utils.StorageUtil.TRAINEES_NAMESPACE;

@Component
public class TraineeProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraineeProcessor.class);

  private final StorageBean storageBean;

  @Autowired
  public TraineeProcessor(StorageBean storageBean) {
    this.storageBean = storageBean;
  }

  public void process(String line) {
    String[] parts = line.split(",");
    if (parts.length < 8) {
      LOGGER.warn("Invalid trainee data: {}", line);
      return;
    }

    try {
      Trainee trainee = new Trainee();
      trainee.setFirstName(parts[1]);
      trainee.setLastName(parts[2]);
      trainee.setUsername(parts[3]);
      trainee.setPassword(parts[4]);
      trainee.setActive(Boolean.parseBoolean(parts[7]));
      trainee.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(parts[5]));
      trainee.setAddress(parts[6]);
      storageBean.getByNameSpace(TRAINEES_NAMESPACE).put(trainee.getUserId(), trainee);
      LOGGER.info("Added Trainee: {}", trainee);
    } catch (Exception e) {
      LOGGER.error("Error processing trainee data: {}", line, e);
    }
  }
}
