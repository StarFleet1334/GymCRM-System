package com.demo.folder.init;

import com.demo.folder.processor.TraineeProcessor;
import com.demo.folder.processor.TrainerProcessor;
import com.demo.folder.processor.TrainingProcessor;
import com.demo.folder.service.FileReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@PropertySource("classpath:application.properties")
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${data.trainee.file.path}")
    private String traineeFilePath;

    @Value("${data.trainer.file.path}")
    private String trainerFilePath;

    @Value("${data.training.file.path}")
    private String trainingFilePath;

    private final FileReaderService fileReaderService;
    private final TraineeProcessor traineeProcessor;
    private final TrainerProcessor trainerProcessor;
    private final TrainingProcessor trainingProcessor;

    @Autowired
    public DataInitializer(FileReaderService fileReaderService,
                           TraineeProcessor traineeProcessor,
                           TrainerProcessor trainerProcessor,
                           TrainingProcessor trainingProcessor) {
        this.fileReaderService = fileReaderService;
        this.traineeProcessor = traineeProcessor;
        this.trainerProcessor = trainerProcessor;
        this.trainingProcessor = trainingProcessor;
    }

    @PostConstruct
    public void initData() {
        processFile(traineeFilePath, traineeProcessor::process);
        processFile(trainerFilePath, trainerProcessor::process);
        processFile(trainingFilePath, trainingProcessor::process);
    }

    private void processFile(String filePath, java.util.function.Consumer<String> processor) {
        List<String> lines = fileReaderService.readLines(filePath);
        if (lines.isEmpty()) {
            LOGGER.warn("No data found in file: {}", filePath);
            return;
        }

        lines.stream().skip(1).forEach(processor);
    }
}
