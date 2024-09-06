package com.demo.folder.actions.trainer;

import com.demo.folder.model.Trainer;

@FunctionalInterface
public interface TrainerUpdater {
  void update(Trainer existingTrainer, Trainer updatedTrainer);
}
