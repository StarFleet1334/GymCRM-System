package com.demo.folder.repository;

import com.demo.folder.entity.Training;
import java.time.LocalDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class TrainingRepository {

  @Autowired
  private SessionFactory sessionFactory;

  public TrainingRepository() {
  }

  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(Training training) {
    getCurrentSession().merge(training);
  }

  public List<Training> findByTrainingDateAfter(Date date) {
    return getCurrentSession()
        .createQuery("FROM Training t WHERE t.trainingDate > :date", Training.class)
        .setParameter("date", date)
        .list();
  }

  public List<Training> findAll() {
    return getCurrentSession().createQuery("FROM Training", Training.class).list();
  }

  public List<Training> findTrainingsForTraineeByCriteria(Long traineeId, LocalDate fromDate,
      LocalDate toDate, String trainerName, String trainingType) {
    StringBuilder queryStr = new StringBuilder("FROM Training t WHERE t.trainee.id = :traineeId");

    if (fromDate != null) {
      queryStr.append(" AND t.trainingDate >= :fromDate");
    }
    if (toDate != null) {
      queryStr.append(" AND t.trainingDate <= :toDate");
    }
    if (trainerName != null && !trainerName.isEmpty()) {
      queryStr.append(" AND t.trainer.user.username = :trainerName");
    }
    if (trainingType != null && !trainingType.isEmpty()) {
      queryStr.append(" AND t.trainingType.trainingTypeName = :trainingType");
    }

    var query = sessionFactory.getCurrentSession().createQuery(queryStr.toString(), Training.class);
    query.setParameter("traineeId", traineeId);

    if (fromDate != null) {
      query.setParameter("fromDate", fromDate);
    }
    if (toDate != null) {
      query.setParameter("toDate", toDate);
    }
    if (trainerName != null && !trainerName.isEmpty()) {
      query.setParameter("trainerName", trainerName);
    }
    if (trainingType != null && !trainingType.isEmpty()) {
      query.setParameter("trainingType", trainingType);
    }

    return query.getResultList();
  }

  public List<Training> findTrainingsForTrainerByCriteria(Long trainerId, LocalDate fromDate,
      LocalDate toDate, String traineeName) {
    StringBuilder queryStr = new StringBuilder("FROM Training t WHERE t.trainer.id = :trainerId");

    if (fromDate != null) {
      queryStr.append(" AND t.trainingDate >= :fromDate");
    }
    if (toDate != null) {
      queryStr.append(" AND t.trainingDate <= :toDate");
    }
    if (traineeName != null && !traineeName.isEmpty()) {
      queryStr.append(" AND t.trainee.user.username = :traineeName");
    }

    var query = sessionFactory.getCurrentSession().createQuery(queryStr.toString(), Training.class);
    query.setParameter("trainerId", trainerId);

    if (fromDate != null) {
      query.setParameter("fromDate", fromDate);
    }
    if (toDate != null) {
      query.setParameter("toDate", toDate);
    }
    if (traineeName != null && !traineeName.isEmpty()) {
      query.setParameter("traineeName", traineeName);
    }

    return query.getResultList();
  }


}
