package com.demo.folder.dao;


import com.demo.folder.model.Training;

import java.util.List;

public interface TrainingDAO {

  void create(Training entity);

  Training read(Long id);

  List<Training> getAll();
}
