package com.demo.folder;

import static com.demo.folder.utils.ProfileUtils.showTraineeServiceOptions;
import static com.demo.folder.utils.ProfileUtils.showTrainerServiceOptions;
import static com.demo.folder.utils.ProfileUtils.showTrainingServiceOptions;

import com.demo.folder.config.SpringConfig;
import com.demo.folder.model.Trainee;
import com.demo.folder.model.Trainer;
import com.demo.folder.model.Training;
import com.demo.folder.model.TrainingType;
import com.demo.folder.service.TraineeService;
import com.demo.folder.service.TrainerService;
import com.demo.folder.service.TrainingService;
import com.demo.folder.system.SystemFacade;
import com.demo.folder.utils.DateUtil;
import java.util.Date;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
  private static Scanner scanner = new Scanner(System.in);
  private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
  private static final TraineeService traineeService = context.getBean(SystemFacade.class).getTraineeService();
  private static final TrainerService trainerService = context.getBean(SystemFacade.class).getTrainerService();
  private static final TrainingService trainingService = context.getBean(SystemFacade.class).getTrainingService();
  private static final String WARNING_MESSAGE = "Invalid choice. Please try again.";
  private static final String EXIT_MESSAGE= "Exit";

  public static void main(String[] args) {
    LOGGER.info("Gym Application started...");
    while (true) {
      LOGGER.info("Select an option:");
      LOGGER.info("1. Manage Trainee Profiles");
      LOGGER.info("2. Manage Trainer Profiles");
      LOGGER.info("3. Manage Training Profiles");
      LOGGER.info("4. "+EXIT_MESSAGE);

      int choice = scanner.nextInt();
      scanner.nextLine();

      switch (choice) {
        case 1:
          processModel("Trainee");
          break;
        case 2:
          processModel("Trainer");
          break;
        case 3:
          processModel("Training");
          break;
        case 4:
          LOGGER.info("Exiting the system. See you later!");
          scanner.close();
          System.exit(0);
          break;
        default:
          LOGGER.warn("Invalid option, please try again.");
      }
    }
  }

  private static void processModel(String entityType) {
    boolean check = true;
    while (check) {
      switch (entityType) {
        case "Trainee":
          showTraineeServiceOptions();
          break;
        case "Trainer":
          showTrainerServiceOptions();
          break;
        case "Training":
          showTrainingServiceOptions();
          break;
      }
      int choice = scanner.nextInt();
      scanner.nextLine();
      check = handleModelChoice(entityType, choice);
    }
  }



  private static boolean handleModelChoice(String entityType, int choice) {
    switch (entityType) {
      case "Trainee":
        return handleChoiceOfTraineeService(choice);
      case "Trainer":
        return handleChoiceOfTrainerService(choice);
      case "Training":
        return handleChoiceOfTrainingService(choice);
      default:
        return false;
    }
  }

  private static boolean handleChoiceOfTraineeService(int choice) {
    switch (choice) {
      case 1:
        traineeService.createTrainee(traineeTL());
        return true;
      case 2:
        selectTraineeById();
        return true;
      case 3:
        traineeService.getAllTrainees().forEach(Trainee::describe);
        return true;
      case 4:
        deleteTraineeById();
        return true;
      case 5:
        updateTraineeById();
        return true;
      case 6:
        return false;
      default:
        LOGGER.warn("Invalid option. Please try again.");
        return true;
    }
  }

  private static void selectTraineeById() {
    LOGGER.info("Enter Trainee ID: ");
    int selectId = scanner.nextInt();
    scanner.nextLine();
    Trainee trainee = traineeService.getTrainee((long) selectId);
    if (trainee != null) {
      trainee.describe();
    } else {
      LOGGER.warn("Trainee with given id does not exist");
    }
  }

  private static void deleteTraineeById() {
    LOGGER.info("Enter Trainee ID to delete: ");
    int deleteId = scanner.nextInt();
    scanner.nextLine();
    Trainee trainee = traineeService.getTrainee((long) deleteId);
    if (trainee != null) {
      traineeService.deleteTrainee((long) deleteId);
      LOGGER.info("Trainee deleted");
    } else {
      LOGGER.warn("Trainee with given id does not exist");
    }
  }

  private static void updateTraineeById() {
    LOGGER.info("Enter Trainee ID to update: ");
    int updateId = scanner.nextInt();
    scanner.nextLine();
    Trainee trainee = traineeService.getTrainee((long) updateId);
    if (trainee != null) {
      traineeService.update((long) updateId, traineeTL());
    } else {
      LOGGER.warn("Trainee with given id does not exist");
    }
  }

  private static boolean handleChoiceOfTrainerService(int choice) {
    switch (choice) {
      case 1:
        trainerService.createTrainer(trainerTL());
        return true;
      case 2:
        updateTrainerById();
        return true;
      case 3:
        selectTrainerById();
        return true;
      case 4:
        trainerService.getAllTrainers().forEach(Trainer::describe);
        return true;
      case 5:
        return false;
      default:
        LOGGER.warn("Invalid option. Please try again.");
        return true;
    }
  }

  private static void selectTrainerById() {
    LOGGER.info("Enter Trainer ID: ");
    int selectId = scanner.nextInt();
    scanner.nextLine();
    Trainer trainer = trainerService.getTrainer((long) selectId);
    if (trainer != null) {
      trainer.describe();
    } else {
      LOGGER.warn("Trainer with given id does not exist");
    }
  }

  private static void updateTrainerById() {
    LOGGER.info("Enter Trainer ID to update: ");
    int updateId = scanner.nextInt();
    scanner.nextLine();
    Trainer trainer = trainerService.getTrainer((long) updateId);
    if (trainer != null) {
      trainerService.update((long) updateId, trainerTL());
    } else {
      LOGGER.warn("Trainer with given id does not exist");
    }
  }

  private static boolean handleChoiceOfTrainingService(int choice) {
    switch (choice) {
      case 1:
        trainingService.createTraining(trainingTL());
        return true;
      case 2:
        selectTrainingById();
        return true;
      case 3:
        trainingService.getAllTrainings().forEach(Training::describe);
        return true;
      case 4:
        return false;
      default:
        LOGGER.warn("Invalid option. Please try again.");
        return true;
    }
  }

  private static void selectTrainingById() {
    LOGGER.info("Enter Training ID: ");
    int selectId = scanner.nextInt();
    scanner.nextLine();
    Training training = trainingService.getTraining((long) selectId);
    if (training != null) {
      training.describe();
    } else {
      LOGGER.warn("Training with given id does not exist");
    }
  }

  private static Trainee traineeTL() {
    Trainee trainee = new Trainee();
    boolean check = true;
    while (check) {
      LOGGER.info("Choose number to change field: ");
      LOGGER.info("1 - firstName");
      LOGGER.info("2 - lastName");
      LOGGER.info("3 - isActive");
      LOGGER.info("4 - DateOfBirth");
      LOGGER.info("5 - Address");
      LOGGER.info("6 - training");
      LOGGER.info("0 - " + EXIT_MESSAGE);

      int choice = scanner.nextInt();
      scanner.nextLine();
      switch (choice) {
        case 1:
          LOGGER.info("Enter firstName: ");
          trainee.setFirstName(scanner.nextLine());
          break;
        case 2:
          LOGGER.info("Enter lastName: ");
          trainee.setLastName(scanner.nextLine());
          break;
        case 3:
          LOGGER.info("Enter isActive (true/false): ");
          trainee.setActive(scanner.nextBoolean());
          scanner.nextLine();
          break;
        case 4:
          LOGGER.info("Enter DateOfBirth (yyyy-MM-dd): ");
          trainee.setDateOfBirth(DateUtil.parseDate(scanner.nextLine()));
          break;
        case 5:
          LOGGER.info("Enter Address: ");
          trainee.setAddress(scanner.nextLine());
          break;
        case 6:
          addTrainingToTrainee(trainee);
          break;
        case 0:
          LOGGER.info("Exiting...");
          check = false;
          break;
        default:
          LOGGER.warn(WARNING_MESSAGE);
      }
    }
    return trainee;
  }

  private static void addTrainingToTrainee(Trainee trainee) {
    LOGGER.info("Here are list of trainings available: ");
    trainingService.getAllTrainings().forEach(training -> {
      if (training.getTraineeId() == null) training.describe();
    });
    LOGGER.info("Provide training id that you want to add:");
    int trainingId = scanner.nextInt();
    scanner.nextLine();
    trainee.addTraining(trainingService.getTraining((long) trainingId));
  }

  private static Trainer trainerTL() {
    Trainer trainer = new Trainer();
    boolean check = true;
    while (check) {
      LOGGER.info("Choose number to change field: ");
      LOGGER.info("1 - firstName");
      LOGGER.info("2 - lastName");
      LOGGER.info("3 - isActive");
      LOGGER.info("4 - specialization");
      LOGGER.info("5 - training");
      LOGGER.info("6 - trainingType");
      LOGGER.info("0 - " + EXIT_MESSAGE);

      int choice = scanner.nextInt();
      scanner.nextLine();
      switch (choice) {
        case 1:
          LOGGER.info("Enter firstName: ");
          trainer.setFirstName(scanner.nextLine());
          break;
        case 2:
          LOGGER.info("Enter lastName: ");
          trainer.setLastName(scanner.nextLine());
          break;
        case 3:
          LOGGER.info("Enter isActive (true/false): ");
          trainer.setActive(scanner.nextBoolean());
          scanner.nextLine(); // Consume newline
          break;
        case 4:
          LOGGER.info("Enter specialization: ");
          trainer.setSpecialization(scanner.nextLine());
          break;
        case 5:
          addTrainingToTrainer(trainer);
          break;
        case 6:
          setTrainingTypeForTrainer(trainer);
          break;
        case 0:
          LOGGER.info("Exiting...");
          check = false;
          break;
        default:
          LOGGER.warn(WARNING_MESSAGE);
      }
    }
    return trainer;
  }

  private static void addTrainingToTrainer(Trainer trainer) {
    LOGGER.info("Provide training id that you want to add:");
    int trainingId = scanner.nextInt();
    scanner.nextLine();
    Training training = trainingService.getTraining((long) trainingId);
    if (training != null) {
      trainer.addTraining(training);
    } else {
      LOGGER.warn("Training with given id does not exist");
    }
  }

  private static void setTrainingTypeForTrainer(Trainer trainer) {
    LOGGER.info("Choose training type:");
    TrainingType[] trainingTypes = TrainingType.values();
    for (int i = 0; i < trainingTypes.length; i++) {
      LOGGER.info(i + ": " + trainingTypes[i]);
    }
    int trainingTypeChoice = scanner.nextInt();
    scanner.nextLine();
    if (trainingTypeChoice >= 0 && trainingTypeChoice < trainingTypes.length) {
      trainer.addTrainingType(trainingTypes[trainingTypeChoice]);
    } else {
      LOGGER.warn("Invalid training type");
    }
  }

  private static Training trainingTL() {
    Training training = new Training();
    boolean check = true;
    while (check) {
      LOGGER.info("Choose number to change field: ");
      LOGGER.info("1 - traineeId");
      LOGGER.info("2 - trainerId");
      LOGGER.info("3 - trainingName");
      LOGGER.info("4 - trainingType");
      LOGGER.info("5 - trainingDate");
      LOGGER.info("6 - trainingDuration");
      LOGGER.info("0 - "+ EXIT_MESSAGE);

      int choice = scanner.nextInt();
      scanner.nextLine();
      switch (choice) {
        case 1:
          setTraineeIdForTraining(training);
          break;
        case 2:
          setTrainerIdForTraining(training);
          break;
        case 3:
          LOGGER.info("Enter trainingName: ");
          training.setTrainingName(scanner.nextLine());
          break;
        case 4:
          setTrainingTypeForTraining(training);
          break;
        case 5:
          LOGGER.info("Enter trainingDate (yyyy-MM-dd): ");
          training.setTrainingDate(DateUtil.parseDate(scanner.nextLine()));
          break;
        case 6:
          LOGGER.info("Enter trainingDuration (in hours): ");
          training.setTrainingDuration(scanner.nextInt());
          scanner.nextLine();
          break;
        case 0:
          LOGGER.info("Exiting...");
          check = false;
          break;
        default:
          LOGGER.warn(WARNING_MESSAGE);
      }
    }
    return training;
  }

  private static void setTraineeIdForTraining(Training training) {
    LOGGER.info("Enter Trainee ID: ");
    int traineeId = scanner.nextInt();
    scanner.nextLine();
    Trainee trainee = traineeService.getTrainee((long) traineeId);
    if (trainee != null) {
      training.setTraineeId((long) traineeId);
    } else {
      LOGGER.warn("Trainee with given id does not exist");
    }
  }

  private static void setTrainerIdForTraining(Training training) {
    LOGGER.info("Enter Trainer ID: ");
    int trainerId = scanner.nextInt();
    scanner.nextLine();
    Trainer trainer = trainerService.getTrainer((long) trainerId);
    if (trainer != null) {
      training.setTrainerId((long) trainerId);
    } else {
      LOGGER.warn("Trainer with given id does not exist");
    }
  }

  private static void setTrainingTypeForTraining(Training training) {
    LOGGER.info("Choose training type:");
    TrainingType[] trainingTypes = TrainingType.values();
    for (int i = 0; i < trainingTypes.length; i++) {
      LOGGER.info(i + ": " + trainingTypes[i]);
    }
    int trainingTypeChoice = scanner.nextInt();
    scanner.nextLine();
    if (trainingTypeChoice >= 0 && trainingTypeChoice < trainingTypes.length) {
      training.setTrainingType(trainingTypes[trainingTypeChoice]);
    } else {
      LOGGER.warn("Invalid training type");
    }
  }
}
