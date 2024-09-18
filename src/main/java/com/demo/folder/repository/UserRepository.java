package com.demo.folder.repository;

import com.demo.folder.entity.User;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository {

  @Autowired
  private SessionFactory sessionFactory;

  public UserRepository() {
  }

  private Session getCurrentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(User user) {
    getCurrentSession().merge(user);
  }

  public User findByUsername(String username) {
    return getCurrentSession()
        .createQuery("FROM User u WHERE u.username = :username", User.class)
        .setParameter("username", username)
        .uniqueResult();
  }

  public User findByUsernameWithAssociations(String username) {
    return getCurrentSession()
        .createQuery(
            "FROM User u LEFT JOIN FETCH u.trainee LEFT JOIN FETCH u.trainerS WHERE u.username = :username",
            User.class)
        .setParameter("username", username)
        .uniqueResult();
  }

  public List<User> findAll() {
    return getCurrentSession().createQuery("FROM User", User.class).list();
  }

  public List<String> findUsernamesStartingWith(String baseUsername) {
    return sessionFactory.getCurrentSession()
        .createQuery("SELECT u.username FROM User u WHERE u.username LIKE :baseUsername",
            String.class)
        .setParameter("baseUsername", baseUsername + "%")
        .list();
  }
}
