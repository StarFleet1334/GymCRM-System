package com.demo.folder.repository;

import com.demo.folder.entity.base.TrainingType;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeRepository {

  @Autowired
  private SessionFactory sessionFactory;

  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(TrainingType trainingType) {
    getCurrentSession().persist(trainingType);
    getCurrentSession().flush();
  }

  public List<TrainingType> findAll() {
    return getCurrentSession().createQuery("FROM TrainingType", TrainingType.class).list();
  }
  public Optional<TrainingType> findById(Long id) {
    TrainingType trainingType = getCurrentSession().get(TrainingType.class, id);
    return Optional.ofNullable(trainingType);
  }

  public TrainingType findByName(String name) {
    return getCurrentSession()
        .createQuery("FROM TrainingType WHERE trainingTypeName = :name", TrainingType.class)
        .setParameter("name", name)
        .uniqueResult();
  }
}
