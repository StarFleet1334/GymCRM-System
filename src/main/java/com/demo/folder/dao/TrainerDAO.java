package com.demo.folder.dao;

import java.util.List;

public interface TrainerDAO<T> {
    void create(T entity);
    T read(Long id);
    void update(T entity);
    List<T> getAll();
}
