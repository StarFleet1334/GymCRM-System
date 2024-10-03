package com.demo.folder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class})
@EnableAspectJAutoProxy
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
