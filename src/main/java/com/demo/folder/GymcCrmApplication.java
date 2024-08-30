package com.demo.folder;

import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.system.SystemFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.demo.folder")
public class GymcCrmApplication implements CommandLineRunner {

    @Autowired
    private SystemFacade systemFacade;

    public static void main(String[] args) {
        SpringApplication.run(GymcCrmApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       systemFacade.getTrainingService().getAllTrainings().forEach(Training::describe);
       systemFacade.getTraineeService().getAllTrainees().forEach(Trainee::describe);
       systemFacade.getTrainerService().getAllTrainers().forEach(Trainer::describe);

    }
}
