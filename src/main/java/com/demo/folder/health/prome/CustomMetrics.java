package com.demo.folder.health.prome;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

  private final Counter traineeRegistrationCounter;
  private final Counter trainerRegistrationCounter;

  public CustomMetrics(MeterRegistry registry) {
    traineeRegistrationCounter = Counter.builder("trainee_registration_count")
        .description("Counts number of trainee registrations")
        .register(registry);
    trainerRegistrationCounter = Counter.builder("trainer_registration_count")
        .description("Counts number of trainer registrations")
        .register(registry);
  }

  public void incrementTraineeRegistrationCount() {
    traineeRegistrationCounter.increment();
  }

  public void incrementTrainerRegistrationCount() {
    trainerRegistrationCounter.increment();
  }
}
