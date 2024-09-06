package com.demo.folder.actions.trainee;

import com.demo.folder.model.Trainee;

@FunctionalInterface
public interface TraineeUpdater {
  void update(Trainee existingTrainee, Trainee updatedTrainee);
}
