package com.demo.folder.repository;

import com.demo.folder.entity.base.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerRepository {

  @Autowired
  private SessionFactory sessionFactory;


  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(Trainer trainer) {
    getCurrentSession().merge(trainer);
  }

  public Trainer findByUsername(String username) {
    return getCurrentSession()
        .createQuery("FROM Trainer t WHERE t.user.username = :username", Trainer.class)
        .setParameter("username", username)
        .uniqueResult();
  }

  public List<Trainer> findAll() {
    return getCurrentSession().createQuery("FROM Trainer", Trainer.class).list();
  }

  public void updateTrainerStatus(Long trainerId, boolean status) {
    Trainer trainer = getCurrentSession().get(Trainer.class, trainerId);
    if (trainer != null) {
      trainer.getUser().setActive(status);
      getCurrentSession().merge(trainer);
    }
  }

  public List<Trainer> findUnassignedTrainers(List<Trainer> assignedTrainers) {
    if (assignedTrainers == null || assignedTrainers.isEmpty()) {
      // If there are no assigned trainers, return all trainers
      return getCurrentSession().createQuery("FROM Trainer", Trainer.class).list();
    } else {
      // Otherwise, return trainers that are not in the assignedTrainers list
      return getCurrentSession()
          .createQuery("FROM Trainer t WHERE t NOT IN :assignedTrainers", Trainer.class)
          .setParameter("assignedTrainers", assignedTrainers)
          .list();
    }
  }


}
