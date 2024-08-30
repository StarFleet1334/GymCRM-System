package com.demo.folder.dao;

import com.demo.folder.model.Trainee;

import java.util.List;

public interface TraineeDAO {
    void create(Trainee entity);
    Trainee read(Long id);
    void update(Trainee entity);
    void delete(Long id);
    List<Trainee> getAll();
}
