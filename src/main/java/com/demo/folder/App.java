package com.demo.folder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class})
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }


  /*


  firsName,lastName validaiton ( + )
  and delete trainer ( + )
  active=true at registering ( + )
  simplify active/deActive ( + )
  format training date ( + )
  duration negativity ( + )
  username should not be changed during updating trainee ( + )
  http://localhost:8080/api/login/change-login should be checked oldPassword match ( + )
  status codes standard ( + )
  on delete return 204 and on register 201 ( + )
  implemented interface for annotations of controller ( + )
  removed exceptions ( + )
  look into each endpoint method and check if throws undetected exception ( - )
   */


}
