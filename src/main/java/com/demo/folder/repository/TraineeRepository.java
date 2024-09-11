package com.demo.folder.repository;

import com.demo.folder.entity.Trainee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TraineeRepository {

  @Autowired
  private SessionFactory sessionFactory;

  public TraineeRepository() {}

  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Transactional
  public void save(Trainee trainee) {
    getCurrentSession().merge(trainee);
  }

  @Transactional(readOnly = true)
  public Trainee findByUsername(String username) {
    return getCurrentSession()
        .createQuery("FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username", Trainee.class)
        .setParameter("username", username)
        .uniqueResult();
  }


  @Transactional(readOnly = true)
  public List<Trainee> findAll() {
    return getCurrentSession().createQuery("FROM Trainee", Trainee.class).list();
  }


  @Transactional
  public void updateTraineeStatus(Long traineeId, boolean status) {
    Trainee trainee = getCurrentSession().get(Trainee.class, traineeId);
    if (trainee != null) {
      trainee.getUser().setActive(status);
      getCurrentSession().merge(trainee);
    }
  }

  @Transactional
  public void deleteById(Long id) {
    Trainee trainee = getCurrentSession().get(Trainee.class, id);
    if (trainee != null) {
      getCurrentSession().remove(trainee);
    }
  }

}
