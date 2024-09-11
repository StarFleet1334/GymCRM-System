package com.demo.folder.repository;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TrainerRepository {

  @Autowired
  private SessionFactory sessionFactory;


  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Transactional
  public void save(Trainer trainer) {
    getCurrentSession().merge(trainer);
  }

  @Transactional(readOnly = true)
  public Trainer findByUsername(String username) {
    return getCurrentSession()
        .createQuery("FROM Trainer t WHERE t.user.username = :username", Trainer.class)
        .setParameter("username", username)
        .uniqueResult();
  }

  @Transactional(readOnly = true)
  public List<Trainer> findAll() {
    return getCurrentSession().createQuery("FROM Trainer", Trainer.class).list();
  }

  @Transactional
  public void updateTrainerStatus(Long trainerId, boolean status) {
    Trainer trainer = getCurrentSession().get(Trainer.class, trainerId);
    if (trainer != null) {
      trainer.getUser().setActive(status);
      getCurrentSession().merge(trainer);
    }
  }

  @Transactional(readOnly = true)
  public List<Trainer> findUnassignedTrainers(List<Trainer> assignedTrainers) {
    return getCurrentSession()
        .createQuery("FROM Trainer t WHERE t NOT IN :assignedTrainers", Trainer.class)
        .setParameter("assignedTrainers", assignedTrainers)
        .list();
  }


}
