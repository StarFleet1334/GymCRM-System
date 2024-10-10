package com.demo.folder.controller.implementation;

import com.demo.folder.controller.skeleton.LoginControllerInterface;
import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.ChangeLoginRequestDTO;
import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.service.LoginAttemptService;
import com.demo.folder.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements LoginControllerInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private LoginAttemptService loginAttemptService;


  public ResponseEntity<String> login(@RequestParam(name = "username") String username,
      @RequestParam(name = "password") String password,
      BindingResult result,
      HttpSession session) {
    if (result.hasErrors()) {
      String errorMessage = result.getFieldError().getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMessage);
    }
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
      loginAttemptService.loginSucceeded(username);
      return ResponseEntity.ok("Login successful");
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }


  @Override
  public ResponseEntity<String> changeLogin(@Valid @RequestBody ChangeLoginRequestDTO changeLoginDTO,
      BindingResult result) {
    try {
      if (result.hasErrors()) {
        String errorMessage = Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
        LOGGER.warn("Validation errors: {}", errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
      }
      User user = userService.authenticate(changeLoginDTO.getUsername(),
          changeLoginDTO.getPassword());
      userService.updatePassword(user, changeLoginDTO.getNewPassword());
      LOGGER.info("Password updated for user: {}", user.getUsername());
      return ResponseEntity.ok("User password successfully updated.");
    } catch (AuthenticationException e) {
      LOGGER.warn("Invalid credentials for user: {}", changeLoginDTO.getUsername());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password.");
    }
  }

  @Override
  public ResponseEntity<String> logout(@RequestParam(name = "username")String username, @RequestParam(name = "password") String password, HttpSession session) {
    try {
      // Authenticate user manually
      User user = userService.authenticate(username, password);
      LOGGER.info("User logged out: {}", user.getUsername());
      if (user != null) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        LOGGER.info("User {} logged out successfully", username);
        return ResponseEntity.ok("Logout successful");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } catch (BadCredentialsException e) {
      LOGGER.warn("Invalid credentials for user: {}", username);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }
}
