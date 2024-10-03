package com.demo.folder.health.prome;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;

@Component
public class TrainerExecutionTime {

  private final Timer methodExecutionTimer;
  private final MeterRegistry meterRegistry;

  public TrainerExecutionTime(MeterRegistry registry) {
    this.meterRegistry = registry;

    methodExecutionTimer = Timer.builder("trainer.registration.time")
        .description("Time taken to execute method: <Create Trainer>")
        .publishPercentileHistogram()
        .register(registry);

  }

  public <T> T recordExecutionTime(Callable<T> callable) {
    Timer.Sample sample = Timer.start(meterRegistry);
    try {
      return callable.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      sample.stop(methodExecutionTimer);
    }
  }
}
