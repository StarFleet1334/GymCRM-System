package com.demo.folder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileReaderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderService.class);

  public List<String> readLines(String filePath) {
    try {
      return Files.readAllLines(Paths.get(filePath));
    } catch (Exception e) {
      LOGGER.error("Error reading file: {}", filePath, e);
      return List.of();
    }
  }
}
