package com.demo.folder.dao;

import java.util.List;

public interface TraineeDAO<T> {
    void create(T entity);
    T read(Long id);
    void update(T entity);
    void delete(Long id);
    List<T> getAll();
}
