package com.demo.folder;

import com.demo.folder.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = SpringConfig.class)

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


public class TT {

  private SessionFactory sessionFactory;

  @BeforeEach
  protected void setUp() throws Exception {
    // A SessionFactory is set up once for an application!
    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
        .configure() // configures settings from hibernate.cfg.xml
        .build();
    try {
      sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
    }
    catch (Exception e) {
      // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
      // so destroy it manually.
      StandardServiceRegistryBuilder.destroy( registry );
    }
  }

  @AfterEach
  protected void tearDown() throws Exception {
    if ( sessionFactory != null ) {
      sessionFactory.close();
    }
  }


  @Test
  @Transactional
  public void test() {
    User s = new User();
    s.setFirstName("Ilia");
    s.setLastName("Sam");
    s.setUsername("NikiMaus");
    s.setPassword("123456");
    s.setActive(true);

    try(Session session = sessionFactory.openSession()) {
      session.beginTransaction();


      session.persist(s);
      session.getTransaction().commit();
    }

  }
}
