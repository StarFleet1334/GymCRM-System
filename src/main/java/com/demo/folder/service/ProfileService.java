package com.demo.folder.service;

import com.demo.folder.entity.Trainee;
import com.demo.folder.entity.Trainer;
import com.demo.folder.entity.Training;
import com.demo.folder.entity.TrainingType;
import com.demo.folder.entity.User;
import com.demo.folder.utils.Generator;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

  private final SessionFactory sessionFactory;
  private final TrainerService trainerService;
  private final TraineeService traineeService;
  private final UserService userService;
  private final TrainingTypeService trainingTypeService;
  private final TrainingService trainingService;

  public ProfileService(SessionFactory sessionFactory, TraineeService traineeService,
      UserService userSService, TrainingTypeService trainingTypeService,
      TrainerService trainerService, TrainingService trainingService) {
    this.sessionFactory = sessionFactory;
    this.traineeService = traineeService;
    this.userService = userSService;
    this.trainingTypeService = trainingTypeService;
    this.trainerService = trainerService;
    this.trainingService = trainingService;
  }


  public void start() {
    Scanner scanner = new Scanner(System.in);
    boolean continueLoop = true;

    while (continueLoop) {
      System.out.println(
          "Do you want to (1) Sign In, (2) Create a New Profile, (3) To Fetch Entity, (4) to to Admin Features and (5) to Exit? (Enter 1, 2, 3, 4 or 5): ");
      int choice = scanner.nextInt();
      scanner.nextLine();

      switch (choice) {
        case 1:
          User authenticatedUser = signIn();
          if (authenticatedUser != null) {
            processUser(authenticatedUser);
          }
          break;
        case 2:
          createProfile();
          break;
        case 3:
          fetchEntity();
          break;
        case 4:
          adminFeatures();
          break;
        case 5:
          System.out.println("Exiting the system.");
          continueLoop = false;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
      }

      if (continueLoop) {
        System.out.println("\nWould you like to perform another action? (yes/no): ");
        String continueChoice = scanner.nextLine();
        if (continueChoice.equalsIgnoreCase("no")) {
          System.out.println("Exiting the system.");
          continueLoop = false;
        }
      }
    }
  }

  private void adminFeatures() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Hello to Admin panel!!!! ");
    System.out.println("Features that you can do include: ");
    System.out.println("1. Select Trainer profile by username");
    System.out.println("2. Select Trainee profile by username");
    System.out.println("3. Activate/Deactivate trainee");
    System.out.println("4. Activate/Deactivate trainer");
    System.out.println("5. Delete trainee profile by username");
    System.out.println("Provide number of your choice: ");

    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        System.out.println("Input username of the trainer to search: ");
        String trainerUserName = scanner.nextLine();
        Trainer trainer = trainerService.findTrainerByUsername(trainerUserName);
        if (trainer != null) {
          displayTrainerDetails(trainer);
        } else {
          System.out.println("Trainer with username '" + trainerUserName + "' not found.");
        }
        break;
      case 2:
        System.out.println("Input username of the trainee to search: ");
        String traineeUserName = scanner.nextLine();
        Trainee trainee = traineeService.findTraineeByUsername(traineeUserName);
        if (trainee != null) {
          displayTraineeDetails(trainee);
        } else {
          System.out.println("Trainee with username '" + traineeUserName + "' not found.");
        }
        break;
      case 3:
        System.out.println("Input username of the trainee to activate/deactivate: ");
        String traineeUsername = scanner.nextLine();
        Trainee selectedTrainee = traineeService.findTraineeByUsername(traineeUsername);
        if (selectedTrainee != null) {
          System.out.println("Do you want to (1) Activate or (2) Deactivate this trainee? ");
          int traineeStatusChoice = scanner.nextInt();
          if (traineeStatusChoice == 1) {
            traineeService.activateTrainee(selectedTrainee.getId());
            System.out.println("Trainee activated.");
          } else if (traineeStatusChoice == 2) {
            traineeService.deactivateTrainee(selectedTrainee.getId());
            System.out.println("Trainee deactivated.");
          }
        } else {
          System.out.println("Trainee not found.");
        }
        break;
      case 4:
        System.out.println("Input username of the trainer to activate/deactivate: ");
        String trainerUsername = scanner.nextLine();
        Trainer selectedTrainer = trainerService.findTrainerByUsername(trainerUsername);
        if (selectedTrainer != null) {
          System.out.println("Do you want to (1) Activate or (2) Deactivate this trainer? ");
          int trainerStatusChoice = scanner.nextInt();
          if (trainerStatusChoice == 1) {
            trainerService.activateTrainer(selectedTrainer.getId());
            System.out.println("Trainer activated.");
          } else if (trainerStatusChoice == 2) {
            trainerService.deactivateTrainer(selectedTrainer.getId());
            System.out.println("Trainer deactivated.");
          }
        } else {
          System.out.println("Trainer not found.");
        }
        break;
      case 5:
        System.out.println("Input username of the trainee to delete: ");
        String traineeToDelete = scanner.nextLine();
        deleteTraineeProfile(traineeToDelete);
        break;

      default:
        System.out.println("Invalid choice. Please try again.");
    }
  }


  private void fetchEntity() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("If you want to fetch all users, press 1 ");
    System.out.println("If you want to fetch all trainers, press 2 ");
    System.out.println("If you want to fetch all trainingTypes, press 3 ");
    System.out.println("If you want to fetch all trainings, press 4 ");
    System.out.println("If you want to fetch all trainees, press 5 ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        userService.getAllUsers().forEach(s -> {
          System.out.println(
              "User: " + s.getUsername() + ", FirstName: " + s.getFirstName() + ", LastName: "
                  + s.getLastName() + ", Password: " + s.getPassword() + ", isActive: "
                  + s.isActive());
        });
        break;
      case 2:
        trainerService.getAllTrainers().forEach(s -> {
          System.out.print(
              "User: " + s.getUser().getUsername() + ", FirstName: " + s.getUser().getFirstName()
                  + ", LastName: " + s.getUser().getLastName() + ", Password: " + s.getUser()
                  .getPassword() + ", Specialization: ");
          if (s.getSpecialization() == null) {
            System.out.println("Specialization is null");
          } else {
            System.out.println(s.getSpecialization().getTrainingTypeName());
          }
          System.out.println(", isActive: " + s.getUser().isActive());
        });
        break;
      case 3:
        trainingTypeService.getAllTrainingTypes().forEach(s -> {
          System.out.println("TrainingTypeName: " + s.getTrainingTypeName());
        });
        break;
      case 4:
        trainingService.getAllTrainings().forEach(s -> {
          if (s.getTrainee() != null) {
            System.out.println("Trainee: " + s.getTrainee().getUser().getUsername());
          }
          if (s.getTrainer() != null) {
            System.out.println("Trainer: " + s.getTrainer().getUser().getUsername());
          }
          System.out.println("TrainingName: " + s.getTrainingName() + ", Training_Date" + s.getTrainingDate() + ", Training_Duration" + s.getTrainingDuration());
        });
        break;
      case 5:
        traineeService.getAllTrainees().forEach(s -> {
          System.out.print(
              "Trainee: " + s.getUser().getUsername() + ", FirstName: " + s.getUser().getFirstName()
                  + ", LastName: " + s.getUser().getLastName() + ",Password " + s.getUser()
                  .getPassword() + ",Date Of Birth:  " + s.getDateOfBirth() + ",Address: "
                  + s.getAddress() + ", isActive: " + s.getUser().isActive());
          if (s.getTrainings() != null) {
            System.out.print(", Trainings: " + s.getTrainings().toString());
          } else {
            System.out.println(", Trainings is null");
          }
          if (s.getTrainers() != null) {
            System.out.print(", Trainers: " + s.getTrainers().toString());
          } else {
            System.out.println(", Trainers is null");
          }
        });
        break;
      default:
        System.out.println("Invalid option");
        break;
    }
    System.out.println("Fetching entity...");
  }


  private User signIn() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter Username: ");
    String username = scanner.nextLine();
    System.out.print("Enter Password: ");
    String password = scanner.nextLine();

    User authenticatedUser = userService.authenticate(username, password);
    if (authenticatedUser != null) {
      System.out.println("Sign-In Successful! Welcome, " + authenticatedUser.getUsername());
      return authenticatedUser;
    } else {
      System.out.println("Invalid credentials. Please try again.");
      return null;
    }
  }


  private void processUser(User authenticatedUser) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Welcome, " + authenticatedUser.getUsername() + " to your profile:");
    System.out.println("Features that you can do include:");
    System.out.println("1. Password change");
    System.out.println("2. Update trainee/trainer profile");

    boolean checkTrainee = false;
    if (authenticatedUser.getTraineeS() != null) {
      System.out.println("You are logged in as a Trainee.");
      checkTrainee = true;
    } else if (authenticatedUser.getTrainerS() != null) {
      System.out.println("You are logged in as a Trainer.");
    }

    if (checkTrainee) {
      System.out.println("3. Get trainer's list that not assigned by username");
      System.out.println("4. Update Trainee's trainers list");
      System.out.println("5. Get Training's List");
      System.out.println("7. Add Training");
    } else {
      System.out.println("6. Get Training's List");
    }

    System.out.println("Include number for features to be processed:");
    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        changePassword(authenticatedUser);
        break;
      case 2:
        System.out.println("Trainee (1) / Trainer (2) ");
        int updateChoice = scanner.nextInt();
        if (updateChoice == 1) {
          modifyTraineeDetails(
              traineeService.findTraineeByUsername(authenticatedUser.getUsername()));
        } else if (updateChoice == 2) {
          modifyTrainerDetails(
              trainerService.findTrainerByUsername(authenticatedUser.getUsername()));
        } else {
          System.out.println("You are neither a Trainee nor a Trainer.");
        }
        break;
      case 3:
        System.out.println("Provide username: ");
        String username = scanner.nextLine();
        getUnassignedTrainersForTrainee(username);
        break;
      case 4:
        System.out.println(
            "Do you want to add a trainer or remove a trainer? (1 for Add / 2 for Remove)");
        int addRemoveChoice = scanner.nextInt();
        scanner.nextLine();
        if (addRemoveChoice == 1) {
          addTrainerToTrainee(authenticatedUser.getTraineeS());
        } else if (addRemoveChoice == 2) {
          // Here we do not add anything to training
          // cause trainee can have trainer but not stated training info
          removeTrainerFromTrainee(authenticatedUser.getTraineeS());
        } else {
          System.out.println("Invalid choice. Please try again.");
        }
        break;
      case 5:
        getTraineeTrainingsListByCriteria(authenticatedUser.getTraineeS());
        break;
      case 6:
        getTrainerTrainingsListByCriteria(authenticatedUser.getTrainerS());
        break;
      case 7:
        addTraining(authenticatedUser.getTraineeS());
        break;

      default:
        System.out.println("Invalid choice. Please try again.");
    }
  }


  private void createProfile() {
    Scanner scanner = new Scanner(System.in);
    System.out.println(
        "Do you want to be a Trainer or Trainee? (Enter 1 for Trainer, 2 for Trainee): ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    if (choice == 1) {
      createTrainerProfile();
    } else if (choice == 2) {
      createTraineeProfile();
    } else {
      System.out.println("Invalid choice. Please try again.");
    }
  }

  private void createTrainerProfile() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter First Name: ");
    String firstName = scanner.nextLine();
    System.out.print("Enter Last Name: ");
    String lastName = scanner.nextLine();

    String baseUsername = Generator.generateUserName(firstName, lastName);
    String password = Generator.generatePassword();
    System.out.println("Generated Base Username: " + baseUsername);
    System.out.println("Generated Password: " + password);

    String uniqueUsername = generateUniqueUsername(baseUsername);

    System.out.println("Final Username: " + uniqueUsername);

    System.out.print("Do you want to enter your specialization? (yes/no): ");
    String specializationChoice = scanner.nextLine();
    TrainingType trainingType = null;

    if (specializationChoice.equalsIgnoreCase("yes")) {
      System.out.println(
          "Do you want to create a new specialization or select from the available ones? (yes for new specialization, no to choose from existing ones)");
      String inChoice = scanner.nextLine();

      if (inChoice.equalsIgnoreCase("yes")) {
        trainingType = new TrainingType();
        System.out.print("Enter new specialization: ");
        String newSpecialization = scanner.nextLine();
        trainingType.setTrainingTypeName(newSpecialization);

        trainingTypeService.createTrainingType(trainingType);

      } else {
        List<TrainingType> trainingTypeList = trainingTypeService.getAllTrainingTypes();
        for (TrainingType type : trainingTypeList) {
          System.out.println(
              "Id: " + type.getId() + " : trainingType: " + type.getTrainingTypeName());
        }
        System.out.println("Provide number from the provided range: ");
        int number = scanner.nextInt();
        trainingType = trainingTypeList.get(number);
        scanner.nextLine();
      }
    }

    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(uniqueUsername);
    user.setPassword(password);
    user.setActive(true);

    Trainer trainer = new Trainer();
    trainer.setSpecialization(trainingType);
    trainer.setUser(user);

    trainerService.createTrainer(trainer);

    System.out.println("Trainer profile created successfully.");
  }


  private void createTraineeProfile() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter First Name: ");
    String firstName = scanner.nextLine();
    System.out.print("Enter Last Name: ");
    String lastName = scanner.nextLine();

    String baseUsername = Generator.generateUserName(firstName, lastName);
    String password = Generator.generatePassword();
    System.out.println("Generated Base Username: " + baseUsername);
    System.out.println("Generated Password: " + password);

    String uniqueUsername = generateUniqueUsername(baseUsername);

    System.out.println("Final Username: " + uniqueUsername);

    System.out.print("Do you want to enter your Date of Birth? (yes/no): ");
    String dobChoice = scanner.nextLine();
    Date dateOfBirth = null;

    if (dobChoice.equalsIgnoreCase("yes")) {
      System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
      String dobInput = scanner.nextLine();
      try {
        dateOfBirth = Date.valueOf(dobInput);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid date format. Skipping Date of Birth.");
      }
    } else {
      System.out.println("Date of Birth skipped.");
    }

    System.out.print("Do you want to enter your Address? (yes/no): ");
    String addressChoice = scanner.nextLine();
    String address = null;

    if (addressChoice.equalsIgnoreCase("yes")) {
      System.out.print("Enter Address: ");
      address = scanner.nextLine();
    } else {
      System.out.println("Address skipped.");
    }

    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(uniqueUsername);
    user.setPassword(password);
    user.setActive(true);

    Trainee trainee = new Trainee();
    trainee.setDateOfBirth(dateOfBirth);
    trainee.setAddress(address);
    trainee.setUser(user);

    traineeService.createTrainee(trainee);

    System.out.println("Trainee profile created successfully.");
  }

  private String generateUniqueUsername(String baseUsername) {
    List<String> existingUsernames = userService.findUsernamesStartingWith(baseUsername);

    if (existingUsernames.isEmpty()) {
      return baseUsername;
    }

    int highestSuffix = 0;
    for (String username : existingUsernames) {
      if (username.matches(baseUsername + "\\d+")) {
        String numberPart = username.substring(baseUsername.length());
        int suffix = Integer.parseInt(numberPart);
        highestSuffix = Math.max(highestSuffix, suffix);
      }
    }

    return baseUsername + (highestSuffix + 1);
  }

  private void displayTrainerDetails(Trainer trainer) {
    System.out.println("=== Trainer Details ===");
    User user = trainer.getUser();
    System.out.println("Username: " + user.getUsername());
    System.out.println("First Name: " + user.getFirstName());
    System.out.println("Last Name: " + user.getLastName());
    System.out.println("Specialization: " + trainer.getSpecialization().getTrainingTypeName());

    System.out.println("=== Trainings Conducted by Trainer ===");
    if (trainer.getTrainings() != null) {
      trainer.getTrainings().forEach(training -> {
        System.out.println("Training ID: " + training.getId());
        System.out.println("Training Name: " + training.getTrainingName());
        System.out.println("Training Duration: " + training.getTrainingDuration() + " days");
      });
    } else {
      System.out.println("No trainings found for trainer ID: " + trainer.getId());
    }
  }

  private void displayTraineeDetails(Trainee trainee) {
    System.out.println("=== Trainee Details ===");
    User user = trainee.getUser();
    System.out.println("Username: " + user.getUsername());
    System.out.println("First Name: " + user.getFirstName());
    System.out.println("Last Name: " + user.getLastName());

    System.out.println("Date of Birth: " + trainee.getDateOfBirth());
    System.out.println("Address: " + trainee.getAddress());

    System.out.println("=== Trainings Attended by Trainee ===");
    if (trainee.getTrainings() != null) {
      trainee.getTrainings().forEach(training -> {
        System.out.println("Training ID: " + training.getId());
        System.out.println("Training Name: " + training.getTrainingName());
        System.out.println("Training Duration: " + training.getTrainingDuration() + " days");
      });
    } else {
      System.out.println("No trainings found for trainer ID: " + trainee.getId());
    }
  }

  private void changePassword(User authenticatedUser) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter your new password: ");
    String newPassword = scanner.nextLine();

    System.out.print("Confirm your new password: ");
    String confirmPassword = scanner.nextLine();

    if (!newPassword.equals(confirmPassword)) {
      System.out.println("Passwords do not match. Please try again.");
      return;
    }

    userService.updatePassword(authenticatedUser, newPassword);
    System.out.println("Password updated successfully.");
  }

  private void deleteTraineeProfile(String traineeUserName) {
    Trainee trainee = traineeService.findTraineeByUsername(traineeUserName);
    if (trainee != null) {
      traineeService.deleteTraineeById(trainee.getId());
      System.out.println("Trainee profile deleted successfully.");
    } else {
      System.out.println("Trainee with username '" + traineeUserName + "' not found.");
    }
  }

  private void modifyTraineeDetails(Trainee trainee) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Modify Trainee Details:");
    System.out.println("1. Date of Birth");
    System.out.println("2. Address");
    System.out.print("Choose the field you want to modify (1 or 2): ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        System.out.print("Enter new Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine();
        try {
          Date dateOfBirth = Date.valueOf(dob);
          trainee.setDateOfBirth(dateOfBirth);
        } catch (IllegalArgumentException e) {
          System.out.println("Invalid date format. Please try again.");
          return;
        }
        break;
      case 2:
        System.out.print("Enter new address: ");
        String address = scanner.nextLine();
        trainee.setAddress(address);
        break;
      default:
        System.out.println("Invalid choice. Please try again.");
    }

    traineeService.updateTrainee(trainee);
    System.out.println("Trainee details updated successfully.");
  }

  private void modifyTrainerDetails(Trainer trainer) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Modify Trainer Details:");
    System.out.println("1. Specialization");
    System.out.print("Choose the field you want to modify (1): ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        System.out.print("Enter new specialization: ");
        String specialization = scanner.nextLine();
        trainer.getSpecialization().setTrainingTypeName(specialization);
        break;
      default:
        System.out.println("Invalid choice. Please try again.");
    }

    trainerService.updateTrainer(trainer);
    System.out.println("Trainer details updated successfully.");
  }

  private void getUnassignedTrainersForTrainee(String traineeUsername) {
    Trainee trainee = traineeService.findTraineeByUsername(traineeUsername);

    if (trainee == null) {
      System.out.println("Trainee not found with username: " + traineeUsername);
      return;
    }

    List<Trainer> allTrainers = trainerService.getAllTrainers();

    List<Trainer> assignedTrainers = trainee.getTrainers();

    List<Trainer> unassignedTrainers = allTrainers.stream()
        .filter(trainer -> !assignedTrainers.contains(trainer))
        .toList();

    if (unassignedTrainers.isEmpty()) {
      System.out.println("All trainers are assigned to this trainee.");
    } else {
      System.out.println("=== Trainers Not Assigned to Trainee ===");
      for (Trainer unassignedTrainer : unassignedTrainers) {
        System.out.println("Trainer Username: " + unassignedTrainer.getUser().getUsername() +
            ", Specialization: " + unassignedTrainer.getSpecialization().getTrainingTypeName());
      }
    }
  }

  private void addTrainerToTrainee(Trainee trainee) {
    Scanner scanner = new Scanner(System.in);
    Hibernate.initialize(trainee.getTrainers());
    List<Trainer> unassignedTrainers = getUnassignedTrainersForTraineePart(
        trainee.getUser().getUsername());

    if (unassignedTrainers.isEmpty()) {
      System.out.println("No trainers are available to add.");
      return;
    }

    System.out.println("Available trainers to add:");
    for (int i = 0; i < unassignedTrainers.size(); i++) {
      Trainer trainer = unassignedTrainers.get(i);
      System.out.println((i + 1) + ". " + trainer.getUser().getUsername() + " - Specialization: "
          + trainer.getSpecialization().getTrainingTypeName());
    }

    System.out.println("Choose a trainer number to add to your list:");
    int selectedTrainerIndex = scanner.nextInt() - 1;

    if (selectedTrainerIndex >= 0 && selectedTrainerIndex < unassignedTrainers.size()) {
      Trainer selectedTrainer = unassignedTrainers.get(selectedTrainerIndex);

      trainee.getTrainers().add(selectedTrainer);

      selectedTrainer.getTrainees().add(trainee);

      traineeService.updateTrainee(trainee);
      trainerService.updateTrainer(selectedTrainer);

      System.out.println(
          "Trainer " + selectedTrainer.getUser().getUsername() + " added to your list.");
    } else {
      System.out.println("Invalid selection. Please try again.");
    }
  }

  private void removeTrainerFromTrainee(Trainee trainee) {
    Scanner scanner = new Scanner(System.in);

    List<Trainer> assignedTrainers = trainee.getTrainers();

    if (assignedTrainers.isEmpty()) {
      System.out.println("No trainers are assigned to you.");
      return;
    }

    System.out.println("Assigned trainers:");
    for (int i = 0; i < assignedTrainers.size(); i++) {
      Trainer trainer = assignedTrainers.get(i);
      System.out.println((i + 1) + ". " + trainer.getUser().getUsername() + " - Specialization: "
          + trainer.getSpecialization().getTrainingTypeName());
    }

    System.out.println("Choose a trainer number to remove from your list:");
    int selectedTrainerIndex = scanner.nextInt() - 1;

    if (selectedTrainerIndex >= 0 && selectedTrainerIndex < assignedTrainers.size()) {
      Trainer selectedTrainer = assignedTrainers.get(selectedTrainerIndex);

      trainee.getTrainers().remove(selectedTrainer);

      selectedTrainer.getTrainees().remove(trainee);

      traineeService.updateTrainee(trainee);
      trainerService.updateTrainer(selectedTrainer);

      System.out.println(
          "Trainer " + selectedTrainer.getUser().getUsername() + " removed from your list.");
    } else {
      System.out.println("Invalid selection. Please try again.");
    }
  }


  private List<Trainer> getUnassignedTrainersForTraineePart(String traineeUsername) {
    Trainee trainee = traineeService.findTraineeByUsername(traineeUsername);

    if (trainee == null) {
      System.out.println("Trainee not found with username: " + traineeUsername);
      return List.of();
    }

    List<Trainer> allTrainers = trainerService.getAllTrainers();
    List<Trainer> assignedTrainers = trainee.getTrainers();

    return allTrainers.stream()
        .filter(trainer -> !assignedTrainers.contains(trainer))
        .toList();
  }

  private void getTraineeTrainingsListByCriteria(Trainee trainee) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Choose the filter criteria:");
    System.out.println("1. From Date (yyyy-MM-dd)");
    System.out.println("2. To Date (yyyy-MM-dd)");
    System.out.println("3. Trainer Name");
    System.out.println("4. Training Type");
    System.out.println("5. Apply filters");

    LocalDate fromDate = null, toDate = null;
    String trainerName = null, trainingType = null;
    boolean filtering = true;

    while (filtering) {
      System.out.print("Select filter number or press 5 to apply filters: ");
      int choice = scanner.nextInt();
      scanner.nextLine();  // consume newline

      switch (choice) {
        case 1:
          try {
            System.out.print("Enter From Date (yyyy-MM-dd): ");
            String fromDateInput = scanner.nextLine();
            if (!fromDateInput.isEmpty()) {
              fromDate = LocalDate.parse(fromDateInput);
            }
          } catch (Exception e) {
            System.out.println("Invalid From Date.");
          }
          break;
        case 2:
          try {
            System.out.print("Enter To Date (yyyy-MM-dd): ");
            String toDateInput = scanner.nextLine();
            if (!toDateInput.isEmpty()) {
              toDate = LocalDate.parse(toDateInput);
            }
          } catch (Exception e) {
            System.out.println("Invalid To Date.");
          }
          break;
        case 3:
          System.out.print("Enter Trainer Name: ");
          trainerName = scanner.nextLine();
          break;
        case 4:
          System.out.print("Enter Training Type: ");
          trainingType = scanner.nextLine();
          break;
        case 5:
          filtering = false;
          break;
        default:
          System.out.println("Invalid choice. Try again.");
          break;
      }
    }

    List<Training> trainings = trainingService.getTrainingsForTraineeByCriteria(trainee.getId(),
        fromDate, toDate, trainerName, trainingType);

    if (trainings.isEmpty()) {
      System.out.println("No trainings found with the selected criteria.");
    } else {
      trainings.forEach(training -> System.out.println(
          "Training: " + training.getTrainingName() + " on " + training.getTrainingDate()));
    }
  }


  private void getTrainerTrainingsListByCriteria(Trainer trainer) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Choose the filter criteria:");
    System.out.println("1. From Date (yyyy-MM-dd)");
    System.out.println("2. To Date (yyyy-MM-dd)");
    System.out.println("3. Trainee Name");
    System.out.println("4. Apply filters");

    LocalDate fromDate = null, toDate = null;
    String traineeName = null;
    boolean filtering = true;

    while (filtering) {
      System.out.print("Select filter number or press 4 to apply filters: ");
      int choice = scanner.nextInt();
      scanner.nextLine();

      switch (choice) {
        case 1:
          try {
            System.out.print("Enter From Date (yyyy-MM-dd): ");
            String fromDateInput = scanner.nextLine();
            if (!fromDateInput.isEmpty()) {
              fromDate = LocalDate.parse(fromDateInput);
            }
          } catch (Exception e) {
            System.out.println("Invalid From Date.");
          }
          break;
        case 2:
          try {
            System.out.print("Enter To Date (yyyy-MM-dd): ");
            String toDateInput = scanner.nextLine();
            if (!toDateInput.isEmpty()) {
              toDate = LocalDate.parse(toDateInput);
            }
          } catch (Exception e) {
            System.out.println("Invalid To Date.");
          }
          break;
        case 3:
          System.out.print("Enter Trainee Name: ");
          traineeName = scanner.nextLine();
          break;
        case 4:
          filtering = false;
          break;
        default:
          System.out.println("Invalid choice. Try again.");
          break;
      }
    }

    List<Training> trainings = trainingService.getTrainingsForTrainerByCriteria(trainer.getId(),
        fromDate, toDate, traineeName);

    if (trainings.isEmpty()) {
      System.out.println("No trainings found with the selected criteria.");
    } else {
      trainings.forEach(training -> System.out.println(
          "Training: " + training.getTrainingName() + " with " + training.getTrainee().getUser()
              .getUsername()));
    }
  }

  private void addTraining(Trainee trainee) {
    Scanner scanner = new Scanner(System.in);

    // Fetch trainers assigned to this trainee
    List<Trainer> assignedTrainers = traineeService.getAssignedTrainers(trainee);

    if (!assignedTrainers.isEmpty()) {
      System.out.println("You have the following trainers assigned:");
      for (int i = 0; i < assignedTrainers.size(); i++) {
        System.out.println(
            (i + 1) + ". " + assignedTrainers.get(i).getUser().getUsername() +
                " - Specialization: " + assignedTrainers.get(i).getSpecialization().getTrainingTypeName());
      }
    } else {
      System.out.println("No trainers are currently assigned to you.");
    }

    System.out.print("Do you want to schedule training with one of your current trainers? (yes/no): ");
    String choice = scanner.nextLine();

    Trainer selectedTrainer = null;

    if (choice.equalsIgnoreCase("yes") && !assignedTrainers.isEmpty()) {
      System.out.print("Select a trainer by number from the list: ");
      int selectedTrainerIndex = scanner.nextInt() - 1;
      scanner.nextLine(); // Consume the newline

      if (selectedTrainerIndex >= 0 && selectedTrainerIndex < assignedTrainers.size()) {
        selectedTrainer = assignedTrainers.get(selectedTrainerIndex);
      } else {
        System.out.println("Invalid selection. Exiting training scheduling.");
        return;
      }
    } else {
      // Fetch trainers not assigned to this trainee
      List<Trainer> unassignedTrainers = traineeService.getUnassignedTrainers(trainee);

      if (unassignedTrainers.isEmpty()) {
        System.out.println("No other trainers available.");
        return;
      }

      System.out.println("Available trainers you can choose from:");
      for (int i = 0; i < unassignedTrainers.size(); i++) {
        System.out.println(
            (i + 1) + ". " + unassignedTrainers.get(i).getUser().getUsername() +
                " - Specialization: " + unassignedTrainers.get(i).getSpecialization().getTrainingTypeName());
      }

      System.out.print("Select a trainer by number from the list: ");
      int selectedTrainerIndex = scanner.nextInt() - 1;
      scanner.nextLine(); // Consume the newline

      if (selectedTrainerIndex >= 0 && selectedTrainerIndex < unassignedTrainers.size()) {
        selectedTrainer = unassignedTrainers.get(selectedTrainerIndex);
      } else {
        System.out.println("Invalid selection. Exiting training scheduling.");
        return;
      }
    }

    System.out.println("You have selected: " + selectedTrainer.getUser().getUsername() + " (" +
        selectedTrainer.getSpecialization().getTrainingTypeName() + ")");

    System.out.print("Enter training name: ");
    String trainingName = scanner.nextLine();

    System.out.print("Enter training duration (in days): ");
    int trainingDuration = scanner.nextInt();
    scanner.nextLine(); // Consume the newline

    System.out.print("Enter training date (yyyy-mm-dd): ");
    String dateInput = scanner.nextLine();
    java.sql.Date trainingDate;
    try {
      trainingDate = java.sql.Date.valueOf(dateInput);
    } catch (Exception e) {
      System.out.println("Invalid date format. Exiting training scheduling.");
      return;
    }

    // Create and save the new training
    Training newTraining = new Training();
    newTraining.setTrainingName(trainingName);
    newTraining.setTrainingDuration(trainingDuration);
    newTraining.setTrainingDate(trainingDate);
    newTraining.setTrainer(selectedTrainer);
    newTraining.setTrainee(trainee);

    trainingService.saveTraining(newTraining);

    System.out.println("Training scheduled successfully with trainer: " + selectedTrainer.getUser().getUsername());
  }



}
