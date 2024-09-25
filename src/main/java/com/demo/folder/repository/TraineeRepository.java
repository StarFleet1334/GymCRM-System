package com.demo.folder.repository;

import com.demo.folder.entity.base.Trainee;
import com.demo.folder.entity.base.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeRepository {

  @Autowired
  private SessionFactory sessionFactory;

  public TraineeRepository() {
  }

  public Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(Trainee trainee) {
    getCurrentSession().merge(trainee);
  }

  public void save(Trainer trainer) {
    getCurrentSession().merge(trainer);
  }

  public Trainee findByUsername(String username) {
    return getCurrentSession()
        .createQuery("FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.user.username = :username",
            Trainee.class)
        .setParameter("username", username)
        .uniqueResult();
  }


  public List<Trainee> findAll() {
    return getCurrentSession().createQuery("FROM Trainee", Trainee.class).list();
  }


  public void updateTraineeStatus(Long traineeId, boolean status) {
    Trainee trainee = getCurrentSession().get(Trainee.class, traineeId);
    if (trainee != null) {
      trainee.getUser().setActive(status);
      getCurrentSession().merge(trainee);
    }
  }

  public void deleteById(Long id) {
    Trainee trainee = getCurrentSession().get(Trainee.class, id);
    if (trainee != null) {
      getCurrentSession().remove(trainee);
    }
  }

}
