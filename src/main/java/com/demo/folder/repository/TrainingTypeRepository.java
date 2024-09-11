package com.demo.folder.repository;

import com.demo.folder.entity.TrainingType;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TrainingTypeRepository {

  @Autowired
  private SessionFactory sessionFactory;

  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Transactional
  public void save(TrainingType trainingType) {
    getCurrentSession().persist(trainingType);
    getCurrentSession().flush();
  }

  @Transactional(readOnly = true)
  public List<TrainingType> findAll() {
    return getCurrentSession().createQuery("FROM TrainingType", TrainingType.class).list();
  }
}
