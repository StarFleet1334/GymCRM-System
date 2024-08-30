package com.demo.folder.dao;


import java.util.List;

public interface TrainingDAO<T> {
    void create(T entity);
    T read(Long id);
    List<T> getAll();
}
