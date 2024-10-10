package com.demo.folder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptService.class);
  private static final int MAX_ATTEMPTS = 3;
  private static final long LOCK_TIME_DURATION = TimeUnit.MINUTES.toMillis(5);
  private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Long> lockCache = new ConcurrentHashMap<>();

  public void loginSucceeded(String username) {
    attemptsCache.remove(username);
    lockCache.remove(username);
  }

  public void clearAttempts(String username) {
    attemptsCache.remove(username);
    lockCache.remove(username);
    LOGGER.info("Cleared login attempts and locks for user {}", username);
  }

  public void loginFailed(String username) {
    int attempts = attemptsCache.getOrDefault(username, 0) + 1;
    attemptsCache.put(username, attempts);
    if (attempts >= MAX_ATTEMPTS) {
      lockCache.put(username, System.currentTimeMillis());
      LOGGER.info("User {} is locked out at {}", username, System.currentTimeMillis());
    }
    LOGGER.info("User {} login attempts: {}", username, attempts);
  }

  public boolean isBlocked(String username) {
    Long lockTimestamp = lockCache.get(username);
    if (lockTimestamp != null) {
      long currentTime = System.currentTimeMillis();
      long timeDiff = currentTime - lockTimestamp;
      long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
      long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(minutes);
      LOGGER.info("Checking block for user {}, time since lock: {} minutes, {} seconds", username, minutes, seconds);
      if (timeDiff > LOCK_TIME_DURATION) {
        LOGGER.info("Block expired for user {}", username);
        lockCache.remove(username);
        attemptsCache.remove(username);
        return false;
      }
      LOGGER.info("User {} is still blocked.", username);
      return true;
    }
    LOGGER.info("No lock timestamp found for user {}, not currently blocked.", username);
    return false;
  }
}
