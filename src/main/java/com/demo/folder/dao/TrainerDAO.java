package com.demo.folder.dao;

import com.demo.folder.model.Trainer;

import java.util.List;

public interface TrainerDAO {

  void create(Trainer entity);

  Trainer read(Long id);

  void update(Trainer entity);

  List<Trainer> getAll();
}
